import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

group = "dev.g000sha256"
version = "0.0.0"

plugins {
    alias(notation = catalog.plugins.g000sha256.sonatypeMavenCentral)
    alias(notation = catalog.plugins.gradle.javaGradlePlugin)
    alias(notation = catalog.plugins.gradle.mavenPublish)
    alias(notation = catalog.plugins.gradle.signing)
    alias(notation = catalog.plugins.jetBrains.dokka)
    alias(notation = catalog.plugins.jetBrains.kotlin)
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    explicitApi()
    jvmToolchain(jdkVersion = 11)

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true
    }

    compilerOptions {
        allWarningsAsErrors = true
        moduleName = "dev.g000sha256.signing"
    }
}

tasks {
    named<Jar>(name = "javadocJar") {
        val taskProvider = named(name = "dokkaGeneratePublicationJavadoc")
        from(taskProvider)
    }
}

gradlePlugin {
    plugins {
        register("signing") {
            id = "dev.g000sha256.signing"
            implementationClass = "dev.g000sha256.signing.SigningPlugin"
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name = "Gradle Signing plugin"
                description = "Configures in-memory PGP keys for the Signing plugin from environment variables or Gradle properties"

                url = "https://github.com/g000sha256/gradle-signing"
                inceptionYear = "2026"

                licenses {
                    license {
                        name = "Apache License 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }

                developers {
                    developer {
                        id = "g000sha256"
                        name = "Georgii Ippolitov"
                        email = "github@g000sha256.dev"
                        url = "https://github.com/g000sha256"
                    }
                }

                scm {
                    connection = "scm:git:https://github.com/g000sha256/gradle-signing.git"
                    developerConnection = "scm:git:ssh://git@github.com/g000sha256/gradle-signing.git"
                    url = "https://github.com/g000sha256/gradle-signing"
                }

                issueManagement {
                    system = "GitHub Issues"
                    url = "https://github.com/g000sha256/gradle-signing/issues"
                }
            }
        }
    }
}

signing {
    val key = getEnvironmentProperty(key = "SIGNING_KEY") ?: getGradleProperty(key = "signing.key")
    val password = getEnvironmentProperty(key = "SIGNING_PASSWORD") ?: getGradleProperty(key = "signing.password")
    useInMemoryPgpKeys(key, password)

    sign(publishing.publications)
}

private fun getEnvironmentProperty(key: String): String? {
    val property = System.getenv(key) ?: return null
    return property.trimOrNull()
}

private fun Project.getGradleProperty(key: String): String? {
    val property = properties.get(key = key) as? String ?: return null
    return property.trimOrNull()
}

private fun String.trimOrNull(): String? {
    val value = trim()
    return value.ifEmpty { null }
}
