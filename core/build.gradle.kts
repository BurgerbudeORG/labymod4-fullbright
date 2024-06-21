dependencies {
  api(project(":api"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileJava {
  sourceCompatibility = JavaVersion.VERSION_21.toString()
  targetCompatibility = JavaVersion.VERSION_21.toString()
}