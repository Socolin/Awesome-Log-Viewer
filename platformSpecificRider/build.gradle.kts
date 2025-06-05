plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.rider-module-conventions")
}

dependencies {
    compileOnly(project(":pluginCore"))
}
