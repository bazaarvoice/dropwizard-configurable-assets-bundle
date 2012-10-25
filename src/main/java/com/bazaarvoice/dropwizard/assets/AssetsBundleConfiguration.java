package com.bazaarvoice.dropwizard.assets;

public interface AssetsBundleConfiguration {
    /** Get the configuration for how the assets should be served. */
    AssetsConfiguration getAssetsConfiguration();
}
