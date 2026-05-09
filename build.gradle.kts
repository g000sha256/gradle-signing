import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

group = "dev.g000sha256"
version = "1.0.0"

plugins {
    alias(notation = catalog.plugins.g000sha256.signing)
    alias(notation = catalog.plugins.g000sha256.sonatypeMavenCentral)
    alias(notation = catalog.plugins.gradle.javaGradlePlugin)
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
        register("release") {
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
    sign(publishing.publications)
}
