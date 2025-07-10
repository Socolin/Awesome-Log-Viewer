plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.module-conventions")
}

dependencies {
    compileOnly(project(":pluginCore"))
    intellijPlatform {
        compatiblePlugin("com.intellij.modules.json")
    }
}
