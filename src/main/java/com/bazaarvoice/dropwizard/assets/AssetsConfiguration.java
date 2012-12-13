package com.bazaarvoice.dropwizard.assets;

import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class AssetsConfiguration {
    @NotNull
    @JsonProperty
    private String cacheSpec = com.yammer.dropwizard.bundles.AssetsBundle.DEFAULT_CACHE_SPEC.toParsableString();

    @NotNull
    @JsonProperty
    private Map<String, String> overrides = Maps.newHashMap();

    /** The caching specification for how to memoize assets. */
    public String getCacheSpec() {
        return cacheSpec;
    }

    public Iterable<Map.Entry<String, String>> getOverrides() {
        return overrides.entrySet();
    }
}
