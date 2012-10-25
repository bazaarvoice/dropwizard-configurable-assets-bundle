package com.bazaarvoice.dropwizard.assets;

import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Environment;

import java.io.File;
import java.net.URL;

/**
 * An assets bundle (like {@link com.yammer.dropwizard.bundles.AssetsBundle}) that utilizes configuration to control
 * how assets are loaded.  Specifically a development mode is supported which will attempt to load assets out of the
 * source hierarchy instead of the classpath.  During development mode if an asset is not able to be found by looking
 * at the source hierarchy it will instead be loaded from the classpath.
 */
public class AssetsBundle implements ConfiguredBundle<AssetsBundleConfiguration> {
    @Override
    public void initialize(AssetsBundleConfiguration serviceConfig, Environment env) {
        AssetsConfiguration config = serviceConfig.getAssetsConfiguration();

        String uriPath = config.getUriPath();
        uriPath = removeTrailing(uriPath, "*");
        uriPath = removeTrailing(uriPath, "/");

        CacheBuilderSpec spec = CacheBuilderSpec.parse(config.getCacheSpec());
        String defaultContentType = config.getDefaultContentType();

        CacheLoader<String, AssetServlet.Asset> loader = CLASSPATH_LOADER;
        String filePath = config.getFilePath();
        if (filePath != null) {
            filePath = removeTrailing(filePath, "*");
            filePath = removeTrailing(filePath, "/");

            // Configure a loader that will source things from the filesystem by default
            loader = new SourceTreeCacheLoader(filePath);

            // Disable caching regardless of what the user told us since caching from the filesystem doesn't
            // make much sense.
            spec = CacheBuilderSpec.disableCaching();
        }

        env.addServlet(new AssetServlet(uriPath, spec, loader, defaultContentType), uriPath + "/*");
    }

    private static String removeTrailing(String s, String c) {
        return s.endsWith(c) ? s.substring(0, s.length() - c.length()) : s;
    }

    /**
     * Loads assets from the classpath.  Similar to the default behavior of
     * {@link com.yammer.dropwizard.bundles.AssetsBundle}.
     */
    private static final CacheLoader<String, AssetServlet.Asset> CLASSPATH_LOADER =
            new CacheLoader<String, AssetServlet.Asset>() {
                @Override
                public AssetServlet.Asset load(String path) throws Exception {
                    // TODO: Make this more like the one in dropwizard
                    URL url = Resources.getResource(path);
                    return new AssetServlet.Asset(Resources.toByteArray(url));
                }
            };

    private static final class SourceTreeCacheLoader extends CacheLoader<String, AssetServlet.Asset> {
        private final String basePath;

        public SourceTreeCacheLoader(String basePath) {
            this.basePath = basePath;
        }

        @Override
        public AssetServlet.Asset load(String path) throws Exception {
            File file = new File(basePath, path);

            return file.exists()
                    ? new AssetServlet.Asset(Files.toByteArray(file), file.lastModified())
                    : CLASSPATH_LOADER.load(path);
        }
    }
}
