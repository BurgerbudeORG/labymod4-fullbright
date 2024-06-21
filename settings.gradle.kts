rootProject.name = "fullbright"

includeBuild("laby-build-logic")

pluginManagement {
  val labyGradlePluginVersion = "0.4.1"
  plugins {
    id("net.labymod.gradle") version (labyGradlePluginVersion)
  }

  buildscript {
    repositories {
      maven("https://dist.labymod.net/api/v1/maven/release/")
      maven("https://repo.spongepowered.org/repository/maven-public")
      mavenCentral()
      mavenLocal()
    }

    dependencies {
      classpath("net.labymod.gradle", "addon", labyGradlePluginVersion)
    }
  }
}

plugins.apply("net.labymod.gradle")

sequenceOf("api", "core").forEach {
  include(":$it")
}