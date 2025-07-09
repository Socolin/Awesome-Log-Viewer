import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.exceptions.MissingVersionException
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("org.jetbrains.intellij.platform")
    alias(libs.plugins.changelog)
    alias(libs.plugins.gradleJvmWrapper)
}

jvmWrapper {
    linuxAarch64JvmUrl = "https://download.oracle.com/java/21/archive/jdk-21.0.3_linux-aarch64_bin.tar.gz"
    linuxX64JvmUrl = "https://download.oracle.com/java/21/archive/jdk-21.0.3_linux-x64_bin.tar.gz"
    macAarch64JvmUrl = "https://download.oracle.com/java/21/archive/jdk-21.0.3_macos-aarch64_bin.tar.gz"
    macX64JvmUrl = "https://download.oracle.com/java/21/archive/jdk-21.0.3_macos-x64_bin.tar.gz"
    windowsX64JvmUrl = "https://download.oracle.com/java/21/archive/jdk-21.0.3_windows-x64_bin.zip"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

val pluginVersion: String by project

version = pluginVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-testing-extension.html
val runRider by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.Rider
    version = libs.versions.riderSdk
    task {
        jvmArgs("-Drider.backend.dotnet.runtime.path=/home/socolin/.dotnet/dotnet")
    }
}
// https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-testing-extension.html
val runRiderDemoStand by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.Rider
    version = libs.versions.riderSdk
    tasks {
        runIde {
            jvmArgs("-Didea.plugins.host=https://master.demo.marketplace.intellij.net/")
        }
    }
}
val runIdeaUltimate by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.IntellijIdeaUltimate
    version = libs.versions.ideaSdk
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.ideaSdk, useInstaller = false)
        pluginVerifier()
        pluginModule(implementation(project(":pluginCore")))
        pluginModule(implementation(project(":processorApplicationInsights")))
        pluginModule(implementation(project(":processorOpenTelemetry")))
        pluginModule(implementation(project(":processorSimpleConsole")))
        pluginModule(implementation(project(":platformSpecificRider")))
        pluginModule(implementation(project(":platformSpecificJava")))
    }
}

intellijPlatform {
    pluginConfiguration {
        val latestChangelog = try {
            changelog.getUnreleased()
        } catch (_: MissingVersionException) {
            changelog.getLatest()
        }
        changeNotes = provider {
            changelog.renderItem(
                latestChangelog
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }
    }
}

tasks {
    wrapper {
        gradleVersion = "8.13"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
    }

    patchPluginXml {
        val latestChangelog = try {
            changelog.getUnreleased()
        } catch (_: MissingVersionException) {
            changelog.getLatest()
        }
        changeNotes.set(provider {
            changelog.renderItem(
                latestChangelog
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        })
    }

    prepareTestSandbox {
        disabledPlugins.add("intellij.platform.ijent.impl") // TODO[#51]: Get rid of this after migration to 2025.1
    }

    runIde {
        jvmArgs("-Xmx1500m")
    }

    test {
        classpath -= classpath.filter {
            (it.name.startsWith("localization-") && it.name.endsWith(".jar")) // TODO[#63]: https://youtrack.jetbrains.com/issue/IJPL-178084/External-plugin-tests-break-due-to-localization-issues
        }

        useTestNG()
        testLogging {
            showStandardStreams = true
            exceptionFormat = TestExceptionFormat.FULL
        }
        environment["LOCAL_ENV_RUN"] = "true"
    }
}
