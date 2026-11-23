plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.snakeyaml:snakeyaml-engine:2.7")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.25.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.25.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    implementation("org.mineskin:java-client-jsoup:3.2.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}

// A jar without the signed profiles builds but cannot enable FancyTags. Fail at packaging instead.
val bundledHeadFiles = listOf("head-textures.properties", "rank-heads.properties")
    .map { layout.projectDirectory.file("src/main/resources/$it").asFile }
tasks.processResources {
    filesMatching("head-textures.properties") {
        // The signed profiles are opaque data. Gradle's template engine cannot parse their long lines.
        filter { it }
    }
    filesMatching("rank-heads.properties") {
        filter { it }
    }
    doFirst {
        check(bundledHeadFiles.all { it.isFile && it.length() > 0 }) {
            "Signed rank heads are missing. Use the verified-head-data from the Paper workflow, or run tools/badges/sign_heads.py before building."
        }
    }
}
