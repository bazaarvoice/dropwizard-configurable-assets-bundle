package com.bazaarvoice.dropwizard.assets;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class AssetsConfiguration {
    @NotNull
    @JsonProperty
    private String cacheSpec = ConfiguredAssetsBundle.DEFAULT_CACHE_SPEC.toParsableString();

    @NotNull
    @JsonProperty
    private Map<String, String> overrides = Maps.newHashMap();
    
    @NotNull
    @JsonProperty
    private Map<String, String> mimeTypes = Maps.newHashMap();

    /** The caching specification for how to memoize assets. */
    public String getCacheSpec() {
        return cacheSpec;
    }

    public Iterable<Map.Entry<String, String>> getOverrides() {
        return Iterables.unmodifiableIterable(overrides.entrySet());
    }

    public Iterable<Map.Entry<String, String>> getMimeTypes() {
        return Iterables.unmodifiableIterable(mimeTypes.entrySet());
    }
}
