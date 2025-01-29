plugins {
  //id("basic-project-convention")
  id("org.cadixdev.licenser") version ("0.6.1")

  id("net.labymod.labygradle")
  id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

version = providers.environmentVariable("VERSION").getOrElse("0.0.1")

labyMod {
  defaultPackageName = "org.burgerbude.labymod.addons.${rootProject.name}" //change this to your main package name (used by all modules)
  addonInfo {
    namespace = "fullbright"
    displayName = "Full Bright"
    author = "BurgerbudeORG"
    version = rootProject.version.toString()
  }

  minecraft {
    registerVersion(versions.toTypedArray()) {
      runs {
        getByName("client") {
          // When the property is set to true, you can log in with a Minecraft account
          // devLogin = true
        }
      }
    }
  }
}

subprojects {
  //plugins.apply("basic-project-convention")
  plugins.apply("net.labymod.labygradle")
  plugins.apply("net.labymod.labygradle.addon")
  plugins.apply("org.cadixdev.licenser")

  license {
    header(rootProject.file("LICENSE-HEADER.txt"))
    newLine.set(true)
  }

  version = rootProject.version
}
