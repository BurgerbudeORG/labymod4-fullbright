plugins {
  id("java-library")
}

group = "org.burgerbude.labymod.addons"
//version = System.getenv().getOrDefault("VERSION", "0.1.0")

repositories {
  mavenCentral()
}

java {
  javaVersion(JavaVersion.VERSION_21)
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {
  compileJava {
    options.encoding = "utf-8"
    targetCompatibility = JavaVersion.VERSION_21.toString()
    sourceCompatibility = JavaVersion.VERSION_21.toString()
  }

  getByName<Test>("test") {
    useJUnitPlatform()
  }
}
