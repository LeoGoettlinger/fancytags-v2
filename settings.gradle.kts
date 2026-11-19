pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
    }
}

rootProject.name = "fancytags"
include("common")
include("plugin")
include("paper")
include("velocity")