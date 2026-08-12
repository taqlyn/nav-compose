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
    }
}

rootProject.name = "taqlyn-nav-compose"

include(":model")
include(":navigation2")
include(":navigation3")
include(":sample-nav2")
include(":sample-nav3")
