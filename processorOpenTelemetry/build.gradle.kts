plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.java-conventions")
    id("awesomeLogViewer.module-conventions")
}

dependencies {
    compileOnly(project(":pluginCore"))
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
        java.srcDir("src/generated/java")
        resources.srcDir("src/main/resources")
    }
}
