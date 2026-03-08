import java.io.FileInputStream
import java.util.*

plugins {
    id(libs.plugins.android.build.library.get().pluginId)
    id("org.jetbrains.kotlin.android") version "2.3.10"
}

val versions = loadAndroidVersions()

android {
    namespace = "com.github.niqdev.mjpeg"
    
    // We need this for Nix flakes
    buildToolsVersion = versions.getProperty("buildToolsVersions")

    defaultConfig {
        compileSdk = versions.getProperty("platformVersions").toInt()
        minSdk = 21
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    lint {
        disable += listOf("Overdraw", "OldTargetApi", "GradleDependency")
        showAll = true
        warningsAsErrors = true
        targetSdk = versions.getProperty("platformVersions").toInt()
    }
    
    packaging {
        resources {
            excludes.add("META-INF/services/javax.annotation.processing.Processor")
        }
    }

    useLibrary("org.apache.http.legacy")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    api("io.reactivex:rxjava:1.3.8") // it"s obsolete
    api("io.reactivex:rxandroid:1.2.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22")
}

// TODO: extract this duplicate code...
/**
 * Loads the default properties, and the user properties. This will also load the
 * secret keys.
 */
fun loadAndroidVersions(): Properties {
    val defaultProps = Properties()
    defaultProps.load(FileInputStream(file("../android-versions.toml")))

    val strippedProps = Properties()
    defaultProps.forEach { k, v ->
        strippedProps.setProperty(k.toString(), v.toString().replace("\"", ""))
    }

    return strippedProps
}
