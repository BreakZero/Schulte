import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import kotlin.apply

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_11
  }
}
dependencies {
  implementation(projects.shared)

  implementation(libs.androidx.activity.compose)

  debugImplementation(libs.compose.uiTooling)
}

android {
  namespace = "org.easy.schulte"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  val keystorePropertiesFile = rootProject.file("keystore/keystore.properties")
  val keystoreProperties = Properties().apply {
    load(keystorePropertiesFile.inputStream())
  }

  signingConfigs {
    create("release") {
      storeFile = rootProject.file("keystore/${keystoreProperties.getProperty("storeFile")}")
      storePassword = keystoreProperties.getProperty("storePassword")
      keyAlias = keystoreProperties.getProperty("keyAlias")
      keyPassword = keystoreProperties.getProperty("keyPassword")
    }
  }

  defaultConfig {
    applicationId = "org.easy.schulte"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
