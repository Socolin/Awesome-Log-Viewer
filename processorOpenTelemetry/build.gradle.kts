plugins {
    id("awesomeLogViewer.kotlin-conventions")
    id("awesomeLogViewer.java-conventions")
    id("awesomeLogViewer.module-conventions")
}

dependencies {
    compileOnly(project(":pluginCore"))
    implementation(platform("io.grpc:grpc-bom:1.73.0"))
    implementation("io.grpc:grpc-netty-shaded")
    implementation("io.grpc:grpc-protobuf")
    // https://mvnrepository.com/artifact/io.grpc/grpc-kotlin-stub
    implementation("io.grpc:grpc-kotlin-stub:1.4.3")
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
