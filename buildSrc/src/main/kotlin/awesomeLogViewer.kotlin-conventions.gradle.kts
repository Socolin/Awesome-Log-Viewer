// SPDX-FileCopyrightText: 2025 Todosaurus contributors <https://github.com/ForNeVeR/Todosaurus>
//
// SPDX-License-Identifier: MIT

plugins {
    id("kotlin")
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    compilerOptions {
        allWarningsAsErrors = true
        // Do not generate bridges for interface default methods (some are @ApiStatus.Internal and flagged by the plugin verifier)
        jvmDefault = org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode.NO_COMPATIBILITY
    }
}
