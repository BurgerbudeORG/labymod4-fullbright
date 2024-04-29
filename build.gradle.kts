plugins {
  id("basic-project-convention")
  id("org.cadixdev.licenser") version ("0.6.1")

  id("net.labymod.gradle")
  id("net.labymod.gradle.addon")
}

labyMod {
  defaultPackageName = "org.burgerbude.labymod.addons.${rootProject.name}" //change this to your main package name (used by all modules)
  addonInfo {
    namespace = "fullbright"
    displayName = "Full Bright"
    author = "BurgerbudeORG"
    version = System.getenv().getOrDefault("VERSION", "0.0.1")
  }

  minecraft {
    registerVersions(
            "1.8.9",
            "1.12.2",
            "1.16.5",
            "1.17.1",
            "1.18.2",
            "1.19.2",
            "1.19.3",
            "1.19.4",
            "1.20.1",
            "1.20.2",
            "1.20.4",
            "1.20.5",
            "1.20.6"
    ) { version, provider ->
      configureRun(provider, version)
    }

    subprojects.forEach {
      if (it.name != "game-runner") {
        filter(it.name)
      }
    }
  }

  addonDev {
    //localRelease()
    productionRelease()
  }
}

fun configureRun(provider: net.labymod.gradle.core.minecraft.provider.VersionProvider, gameVersion: String) {
  provider.runConfiguration {
    mainClass = "net.minecraft.launchwrapper.Launch"
    jvmArgs("-Dnet.labymod.running-version=${gameVersion}")
    jvmArgs("-Dmixin.debug=true")
    jvmArgs("-Dnet.labymod.debugging.all=true")
    jvmArgs("-Dmixin.env.disableRefMap=true")

    args("--tweakClass", "net.labymod.core.loader.vanilla.launchwrapper.LabyModLaunchWrapperTweaker")
    args("--labymod-dev-environment", "true")
    args("--addon-dev-environment", "true")
  }

  provider.javaVersion = when (gameVersion) {
    else -> {
      JavaVersion.VERSION_21
    }
  }

  provider.mixin {
    val mixinMinVersion = when (gameVersion) {
      "1.8.9", "1.12.2", "1.16.5" -> {
        "0.6.6"
      }

      else -> {
        "0.8.2"
      }
    }

    minVersion = mixinMinVersion
  }
}


subprojects {
  apply(plugin = "basic-project-convention")
  apply(plugin = "net.labymod.gradle")
  apply(plugin = "net.labymod.gradle.addon")
  apply(plugin = "org.cadixdev.licenser")

  license {
    header(rootProject.file("LICENSE-HEADER.txt"))
    newLine.set(true)
  }
}