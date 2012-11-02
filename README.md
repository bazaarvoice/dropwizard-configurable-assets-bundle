# Configurable Assets Bundle for Dropwizard

This GitHub repository contains a drop-in replacement for Yammer's `AssetsBundle` class that allows for a better
developer experience.  Developers can use the `ConfiguredAssetsBundle` class anywhere they would use a `AssetsBundle`
in their Dropwizard applications and take advantage of the ability to specify redirects for URIs to that loads them from
disk instead of the classpath.  This allows developers to edit browser-interpreted files and reload them without needing
to recompile source.

## Maven Setup

```xml
<dependency>
  <groupId>com.bazaarvoice.dropwizard</groupId>
  <artifactId>dropwizard-configurable-assets-bundle</artifactId>
  <version>0.1.0</version>
</dependency>
```