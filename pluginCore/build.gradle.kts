plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.module-conventions")
}

dependencies {
    intellijPlatform {
        compatiblePlugin("com.intellij.modules.json")
        bundledModule("intellij.platform.debugger")
    }

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.11")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off"
    )
}
