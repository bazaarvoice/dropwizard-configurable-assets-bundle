package com.bazaarvoice.dropwizard.assets;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * An assets bundle (like {@link com.yammer.dropwizard.bundles.AssetsBundle}) that utilizes configuration to provide the
 * ability to override how assets are loaded and cached.  Specifying an override is useful during the development phase
 * to allow assets to be loaded directly out of source directories instead of the classpath and to force them to not be
 * cached by the browser or the server.  This allows developers to edit an asset, save and then immediately refresh the
 * web browser and see the updated assets.  No compilation or copy steps are necessary.
 * <p/>
 * In order to take advantage TODO: finish me
 * <p/>
 */
public class ConfiguredAssetsBundle implements ConfiguredBundle<AssetsBundleConfiguration> {
    private static final String DEFAULT_PATH = AssetsBundle.DEFAULT_PATH;
    private static final CacheBuilderSpec DEFAULT_CACHE_SPEC = AssetsBundle.DEFAULT_CACHE_SPEC;

    private final String resourcePath;
    private final CacheBuilderSpec cacheBuilderSpec;
    private final String uriPath;

    /**
     * Creates a new {@link ConfiguredAssetsBundle} which serves up static assets from
     * {@code src/main/resources/assets/*} as {@code /assets/*}.
     *
     * @see ConfiguredAssetsBundle#ConfiguredAssetsBundle(String, CacheBuilderSpec)
     */
    public ConfiguredAssetsBundle() {
        this(DEFAULT_PATH, DEFAULT_CACHE_SPEC);
    }

    /**
     * Creates a new {@link ConfiguredAssetsBundle} which will configure the service to serve the static files
     * located in {@code src/main/resources/${path}} as {@code /${path}}. For example, given a
     * {@code path} of {@code "/assets"}, {@code src/main/resources/assets/example.js} would be
     * served up from {@code /assets/example.js}.
     *
     * @param path the classpath and URI root of the static asset files
     * @see ConfiguredAssetsBundle#ConfiguredAssetsBundle(String, CacheBuilderSpec)
     */
    public ConfiguredAssetsBundle(String path) {
        this(path, DEFAULT_CACHE_SPEC, path);
    }

    /**
     * Creates a new {@link ConfiguredAssetsBundle} which will configure the service to serve the static files
     * located in {@code src/main/resources/${resourcePath}} as {@code /${uriPath}}. For example, given a
     * {@code resourcePath} of {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from {@code /js/example.js}.
     *
     * @param resourcePath the resource path (in the classpath) of the static asset files
     * @param uriPath      the uri path for the static asset files
     * @see ConfiguredAssetsBundle#ConfiguredAssetsBundle(String, CacheBuilderSpec)
     */
    public ConfiguredAssetsBundle(String resourcePath, String uriPath) {
        this(resourcePath, DEFAULT_CACHE_SPEC, uriPath);
    }

    /**
     * Creates a new {@link ConfiguredAssetsBundle} which will configure the service to serve the static files
     * located in {@code src/main/resources/${path}} as {@code /${path}}. For example, given a
     * {@code path} of {@code "/assets"}, {@code src/main/resources/assets/example.js} would be
     * served up from {@code /assets/example.js}.
     *
     * @param resourcePath     the resource path (in the classpath) of the static asset files
     * @param cacheBuilderSpec the spec for the cache builder
     */
    public ConfiguredAssetsBundle(String resourcePath, CacheBuilderSpec cacheBuilderSpec) {
        this(resourcePath, cacheBuilderSpec, resourcePath);
    }

    /**
     * Creates a new {@link ConfiguredAssetsBundle} which will configure the service to serve the static files
     * located in {@code src/main/resources/${resourcePath}} as {@code /${uriPath}}. For example, given a
     * {@code resourcePath} of {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from {@code /js/example.js}.
     *
     * @param resourcePath     the resource path (in the classpath) of the static asset files
     * @param cacheBuilderSpec the spec for the cache builder
     * @param uriPath          the uri path for the static asset files
     */
    public ConfiguredAssetsBundle(String resourcePath, CacheBuilderSpec cacheBuilderSpec, String uriPath) {
        checkArgument(resourcePath.startsWith("/"), "%s is not an absolute path", resourcePath);
        checkArgument(!"/".equals(resourcePath), "%s is the classpath root", resourcePath);
        this.resourcePath = resourcePath.endsWith("/") ? resourcePath : (resourcePath + '/');
        this.uriPath = uriPath.endsWith("/") ? uriPath : (uriPath + '/');
        this.cacheBuilderSpec = cacheBuilderSpec;
    }

    @Override
    public void initialize(AssetsBundleConfiguration bundleConfig, Environment env) {
        AssetsConfiguration config = bundleConfig.getAssetsConfiguration();

        // Let the cache spec from the configuration override the one specified in the code
        CacheBuilderSpec spec = (config.getCacheSpec() != null)
                ? CacheBuilderSpec.parse(config.getCacheSpec())
                : cacheBuilderSpec;

        Iterable<Map.Entry<String, String>> overrides = config.getOverrides();

        env.addServlet(new AssetServlet(resourcePath, spec, uriPath, overrides), uriPath + "*");
    }
}
