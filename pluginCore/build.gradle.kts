plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.module-conventions")
}

dependencies {
    intellijPlatform {
        compatiblePlugin("com.intellij.modules.json")
    }

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.2")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off"
    )
}
