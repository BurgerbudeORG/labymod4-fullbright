import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.project

fun JavaPluginExtension.javaVersion(javaVersion: JavaVersion) {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

fun DependencyHandler.apiProject(rootProject: Project, name: String, configuration: String? = null) {
  add("api", project(":${rootProject.name}-$name", configuration))
}