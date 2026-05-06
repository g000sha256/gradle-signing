# Gradle Signing plugin

[![Maven Central](https://img.shields.io/maven-central/v/dev.g000sha256/gradle-signing?label=Maven%20Central&labelColor=171C35&color=E38E33)](https://central.sonatype.com/artifact/dev.g000sha256/gradle-signing)

This Gradle plugin simplifies signing your artifacts. It automatically applies the standard
[Signing Plugin](https://docs.gradle.org/current/userguide/signing_plugin.html) and configures
[in-memory PGP keys](https://docs.gradle.org/current/userguide/signing_plugin.html#sec:in-memory-keys)
from environment variables or Gradle properties.

## Initialization

```kotlin
plugins {
    id("dev.g000sha256.signing") version "<latest>"
}
```

## Configuration

### Add keys

For CI/CD, the plugin reads the credentials from environment variables:

```shell
SIGNING_KEY=<your signing key>
SIGNING_PASSWORD=<your signing password>
# optional
SIGNING_KEY_ID=<your signing key id>
```

You can also store the credentials in your private Gradle properties file (`~/.gradle/gradle.properties`):

```properties
signing.key=<your signing key>
signing.password=<your signing password>
# optional
signing.keyId=<your signing key id>
```

> [!NOTE]
> The plugin reads each value in the following order: environment variable, then Gradle property.
> If a key or password cannot be resolved, the plugin skips configuring in-memory keys - the standard Signing plugin's
> file-based credentials (`signing.keyId`, `signing.password`, `signing.secretKeyRingFile`) still work.

### Sign

A specific publication:

```kotlin
signing {
    val publication = publishing.publications["<your publication name>"]
    sign(publication)
}
```

or all publications:

```kotlin
signing {
    sign(publishing.publications)
}
```

See [signing publications](https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signing_publications) in the
Gradle docs for more options.

### Override

Your own `signing` block runs after this plugin, so you can replace the configured keys:

```kotlin
signing {
    useInMemoryPgpKeys("<your signing key>", "<your signing password>")
}
```
