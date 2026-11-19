plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.snakeyaml:snakeyaml-engine:2.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    implementation("org.mineskin:java-client-jsoup:3.2.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}