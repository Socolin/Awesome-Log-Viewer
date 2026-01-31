plugins {
    id("org.jetbrains.intellij.platform.module")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        rider(versionCatalogs.named("libs").findVersion("riderSdk").get().requiredVersion) {
            useInstaller = false
        }
    }
}
