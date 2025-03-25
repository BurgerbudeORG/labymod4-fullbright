rootProject.name = "fullbright"

includeBuild("laby-build-logic")

pluginManagement {
  val labyGradlePluginVersion = "0.5.9"

  buildscript {
    repositories {
      maven("https://dist.labymod.net/api/v1/maven/release/")
      maven("https://maven.neoforged.net/releases/")
      maven("https://maven.fabricmc.net/")
      gradlePluginPortal()
      mavenCentral()
      mavenLocal()
    }

    dependencies {
      classpath("net.labymod.gradle", "common", labyGradlePluginVersion)
    }
  }
}

plugins.apply("net.labymod.labygradle.settings")

sequenceOf("api", "core").forEach {
  include(":$it")
}