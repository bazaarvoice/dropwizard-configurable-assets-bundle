package com.bazaarvoice.dropwizard.assets;

import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class AssetsConfiguration {
    @NotNull
    @JsonProperty
    private String cacheSpec = com.yammer.dropwizard.bundles.AssetsBundle.DEFAULT_CACHE_SPEC.toParsableString();

    @NotNull
    @JsonProperty
    private List<Map<String, String>> overrides = Lists.newArrayList();

    /** The caching specification for how to memoize assets. */
    public String getCacheSpec() {
        return cacheSpec;
    }

    public Iterable<Map.Entry<String, String>> getOverrides() {
        List<Map.Entry<String, String>> entries = Lists.newArrayList();
        for (Map<String, String> map : overrides) {
            entries.addAll(map.entrySet());
        }
        return entries;
    }
}
