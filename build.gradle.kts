plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
    id("net.neoforged.licenser") version "0.7.5"
}

group = "dev.vatten"

repositories {
    mavenCentral()
}

dependencies {

}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "net.neoforged.licenser")

    group = rootProject.group
    val pluginId = rootProject.name
    val pluginName = rootProject.property("pluginName")
    version = rootProject.version

    repositories {
        mavenCentral()

        maven {
            url = uri("https://repo.extendedclip.com/releases/")
        }

        maven {
            name = "inventive-repo"
            url = uri("https://repo.inventivetalent.org/repository/public/")
        }
    }

    dependencies {
        implementation("de.exlll:configlib-yaml:4.6.3")
        compileOnly("net.kyori:adventure-api:4.25.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.0.1")

        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }

    if (project.name in listOf("common", "plugin")) {
        tasks.withType<JavaCompile>().configureEach { options.release.set(21) }
    }

    tasks.shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("${pluginName}-${version}.jar")
    }

    license {
        header.set(resources.text.fromFile(rootProject.file("HEADER")))
        properties {
            set("name", "vatten <vatten.dev>")
            set("year", 2026)
        }
    }

    tasks.processResources {
        filesMatching("*") {
            expand(
                "pluginId" to pluginId,
                "pluginName" to pluginName,
                "version" to version)
        }
    }
}