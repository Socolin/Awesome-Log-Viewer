plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.rider-module-conventions")
}

dependencies {
    compileOnly(project(":pluginCore"))
    intellijPlatform {
        bundledModule("intellij.rd.client")
        bundledModule("intellij.rider.rdclient.dotnet")
        bundledModule("intellij.platform.debugger")
        bundledModule("intellij.platform.debugger.impl")
        bundledModule("intellij.rd.client.debugger")
    }
}
