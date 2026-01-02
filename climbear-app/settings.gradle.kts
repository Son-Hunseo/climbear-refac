pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // KaKao Login
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
        // Mapbox Maven repository 추가 (아래 코드)
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                // username은 항상 "mapbox"로 고정
                username = "mapbox"
                // gradle.properties에 저장한 토큰 사용
                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            }
        }
    }
}

rootProject.name = "Climbear"
include(":app")