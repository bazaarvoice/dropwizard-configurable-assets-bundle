# Configurable Assets Bundle for Dropwizard

This GitHub repository contains a drop-in replacement for Yammer's `AssetsBundle` class that allows for a better
developer experience.  Developers can use the `ConfiguredAssetsBundle` class anywhere they would use a `AssetsBundle`
in their Dropwizard applications and take advantage of the ability to specify redirects for URIs to that loads them from
disk instead of the classpath.  This allows developers to edit browser-interpreted files and reload them without needing
to recompile source.

This version is compatible with dropwizard 0.7.X.

## Maven Setup

```xml
<dependency>
  <groupId>com.bazaarvoice.dropwizard</groupId>
  <artifactId>dropwizard-configurable-assets-bundle</artifactId>
  <version>0.2.1</version>
</dependency>
```

## Getting Started

Implement the AssetsBundleConfiguration:
```java
public class SampleConfiguration extends Configuration implements AssetsBundleConfiguration {
  @Valid
  @NotNull
  @JsonProperty
  private final AssetsConfiguration assets = new AssetsConfiguration();

  @Override
  public AssetsConfiguration getAssetsConfiguration() {
    return assets;
  }
}
```

Add the redirect bundle:
```java
public class SampleService extends Application<SampleConfiguration> {
    public static void main(String[] args) throws Exception {
        new SampleService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SampleConfiguration> bootstrap) {
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/dashboard/"));
    }

    @Override
    public void run(SampleConfiguration configuration, Environment environment) {
        ...
    }
}
```

A sample local development config:
```yml
assets:
  overrides:
    /dashboard: src/main/resources/assets/
  mimeTypes:
    woff: application/font-woff
```

You can override multiple external folders with a single configuration in a following way:
```yml
assets:
  overrides:
    /dashboard/assets: /some/absolute/path/with/assets/
    /dashboard/images: /some/different/absolute/path/with/images
```
