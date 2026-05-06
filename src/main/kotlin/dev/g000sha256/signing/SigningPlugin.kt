/*
 * Copyright 2026 Georgii Ippolitov (g000sha256)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.g000sha256.signing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin as GradleSigningPlugin

/**
 * Gradle plugin that applies the standard Signing plugin and configures in-memory PGP keys
 * from environment variables or Gradle properties.
 *
 * For each value, the plugin checks the environment variable first, then the Gradle property:
 *  - key: `SIGNING_KEY` / `signing.key`
 *  - password: `SIGNING_PASSWORD` / `signing.password`
 *  - key id (optional): `SIGNING_KEY_ID` / `signing.keyId`
 *
 * If both the key and the password are resolved, the plugin calls `useInMemoryPgpKeys`. Otherwise
 * it skips the in-memory configuration, leaving the standard Signing plugin's file-based
 * credentials (`signing.keyId`, `signing.password`, `signing.secretKeyRingFile`) intact.
 *
 * A user can override the configuration by calling `useInMemoryPgpKeys` in their own
 * `signing { ... }` block, which runs after this plugin and takes precedence.
 */
public class SigningPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(GradleSigningPlugin::class.java)

        val key = target.getProperty(environmentKey = "SIGNING_KEY", gradleKey = "signing.key") ?: return
        val password = target.getProperty(environmentKey = "SIGNING_PASSWORD", gradleKey = "signing.password") ?: return
        val keyId = target.getProperty(environmentKey = "SIGNING_KEY_ID", gradleKey = "signing.keyId")

        val signingExtension = target.extensions.getByType(SigningExtension::class.java)
        if (keyId != null) {
            signingExtension.useInMemoryPgpKeys(keyId, key, password)
        } else {
            signingExtension.useInMemoryPgpKeys(key, password)
        }
    }

    private fun Project.getProperty(environmentKey: String, gradleKey: String): String? {
        return getTrimmedEnvironmentProperty(key = environmentKey) ?: getTrimmedGradleProperty(key = gradleKey)
    }

    private fun getTrimmedEnvironmentProperty(key: String): String? {
        val property = System.getenv(key) ?: return null
        return property.trimOrNull()
    }

    private fun Project.getTrimmedGradleProperty(key: String): String? {
        val property = properties.get(key = key) as? String ?: return null
        return property.trimOrNull()
    }

    private fun String.trimOrNull(): String? {
        val value = trim()
        return value.ifEmpty { null }
    }
}
