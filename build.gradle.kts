plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidMultiplatformLibrary) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.kotlinSerialization) apply false
  alias(libs.plugins.sqldelight) apply false
}

val ktlint by configurations.creating

dependencies {
  ktlint("com.pinterest.ktlint:ktlint-cli:1.8.0") {
    attributes {
      attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
    }
  }
}

val ktlintCheck by tasks.registering(JavaExec::class) {
  group = LifecycleBasePlugin.VERIFICATION_GROUP
  description = "Check Kotlin code style."
  classpath = ktlint
  mainClass.set("com.pinterest.ktlint.Main")
  args(
    "**/src/**/*.kt",
    "**.kts",
    "!**/build/**",
  )
}

tasks.register<JavaExec>("ktlintFormat") {
  group = "formatting"
  description = "Fix Kotlin code style deviations."
  classpath = ktlint
  mainClass.set("com.pinterest.ktlint.Main")
  args(
    "-F",
    "**/src/**/*.kt",
    "**.kts",
    "!**/build/**",
  )
}
