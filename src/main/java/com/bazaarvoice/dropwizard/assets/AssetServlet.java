package com.bazaarvoice.dropwizard.assets;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet responsible for serving assets to the caller.  This is basically completely stolen from
 * {@link com.yammer.dropwizard.servlets.AssetServlet} with the exception of externalizing some of the defaults as well
 * as the {@link LoadingCache}.
 *
 * @see com.yammer.dropwizard.servlets.AssetServlet
 */
class AssetServlet extends HttpServlet {
    private static final MimeTypes MIME_TYPES = new MimeTypes();

    private final String uriPrefix;
    private final LoadingCache<String, Asset> cache;
    private final String defaultContentType;

    AssetServlet(String uriPrefix, CacheBuilderSpec spec, CacheLoader<String, Asset> loader,
                 String defaultContentType) {
        this.uriPrefix = uriPrefix;
        this.cache = CacheBuilder.from(spec).build(loader);
        this.defaultContentType = defaultContentType;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getRequestURI();
        if (!path.startsWith(uriPrefix)) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        path = path.substring(uriPrefix.length());

        try {
            Asset asset = cache.getUnchecked(path);
            if (asset == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Check ETags to see if the resourceBytes has changed...
            String ifNoneMatch = req.getHeader(HttpHeaders.IF_NONE_MATCH);
            if (ifNoneMatch != null && asset.getETag().equals(ifNoneMatch)) {
                res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            // Check the modification time...
            String ifModifiedSince = req.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (ifModifiedSince != null && Long.parseLong(ifModifiedSince) >= asset.getLastModifiedTime()) {
                res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            res.setDateHeader(HttpHeaders.LAST_MODIFIED, asset.getLastModifiedTime());
            res.setHeader(HttpHeaders.ETAG, asset.getETag());

            Buffer contentType = MIME_TYPES.getMimeByExtension(req.getRequestURI());
            if (contentType == null) {
                res.setContentType(defaultContentType);
            } else {
                res.setContentType(contentType.toString());
            }

            ServletOutputStream out = res.getOutputStream();
            try {
                out.write(asset.getResourceBytes());
            } finally {
                out.close();
            }
        } catch (RuntimeException e) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    static final class Asset {
        private final byte[] resourceBytes;
        private final String eTag;
        private final long lastModifiedTime;

        public Asset(byte[] bytes) {
            this(bytes, System.currentTimeMillis());
        }

        public Asset(byte[] bytes, long ts) {
            resourceBytes = bytes;
            eTag = Hashing.murmur3_128().hashBytes(bytes).toString();
            lastModifiedTime = round(ts);
        }

        public byte[] getResourceBytes() {
            return resourceBytes;
        }

        public String getETag() {
            return eTag;
        }

        public long getLastModifiedTime() {
            return lastModifiedTime;
        }

        private long round(long ts) {
            // Zero out the millis since the timestamp we get back from If-Modified-Since will not have them
            return (ts / 1000) * 1000;
        }
    }
}
