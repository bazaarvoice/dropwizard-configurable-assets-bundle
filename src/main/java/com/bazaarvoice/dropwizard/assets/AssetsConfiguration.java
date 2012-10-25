package com.bazaarvoice.dropwizard.assets;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jetty.http.MimeTypes;

import javax.validation.constraints.NotNull;

public class AssetsConfiguration {
    @NotNull
    @JsonProperty
    private String uriPath = com.yammer.dropwizard.bundles.AssetsBundle.DEFAULT_PATH;

    @NotNull
    @JsonProperty
    private String defaultContentType = MimeTypes.TEXT_HTML;

    @NotNull
    @JsonProperty
    private String cacheSpec = com.yammer.dropwizard.bundles.AssetsBundle.DEFAULT_CACHE_SPEC.toParsableString();

    @JsonProperty
    private String filePath = null;

    /** The prefix on all URI paths for static assets. */
    public String getUriPath() {
        return uriPath;
    }

    /** The default content type to use for assets that have an unrecognized extension. */
    public String getDefaultContentType() {
        return defaultContentType;
    }

    /** The caching specification for how to memoize assets. */
    public String getCacheSpec() {
        return cacheSpec;
    }

    /** The base path in the filesystem to load assets from. */
    public String getFilePath() {
        return filePath;
    }
}
