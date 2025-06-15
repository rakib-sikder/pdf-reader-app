// In your settings.gradle.kts file (at the root of your project)

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // This line is STILL absolutely essential
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "pdfreaderapp"
include(":app")