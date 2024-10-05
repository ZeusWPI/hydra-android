/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.util.Properties
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.gms.googleservices.GoogleServicesPlugin
import java.io.FileInputStream

plugins {
    id(libs.plugins.android.build.tool.get().pluginId)
    id("be.ugent.zeus.hydra.licenses")
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics.gradle)
}

// Read our properties, see bottom for details.
val props = loadProperties()
val versions = loadAndroidVersions()

android {
    namespace = "be.ugent.zeus.hydra"
    
    // We need this for Nix flakes
    buildToolsVersion = versions.getProperty("buildToolsVersions")

    defaultConfig {
        compileSdk = versions.getProperty("platformVersions").toInt()
        applicationId = "be.ugent.zeus.hydra"
        minSdk = 21
        targetSdk = 34
        versionCode = 37300
        versionName = "3.7.3"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("nl", "en")

        // For a description of what these do, see the config.properties file.
        buildConfigField("boolean", "DEBUG_HOME_STREAM_PRIORITY", props.getProperty("hydra.debug.home.stream.priority"))
        buildConfigField("boolean", "DEBUG_HOME_STREAM_STALL", props.getProperty("hydra.debug.home.stream.stall"))
        buildConfigField("boolean", "DEBUG_ENABLE_STRICT_MODE", props.getProperty("hydra.debug.strict_mode"))
        buildConfigField("boolean", "DEBUG_ENABLE_ALL_SPECIALS", props.getProperty("hydra.debug.home.stream.specials"))
        buildConfigField("boolean", "DEBUG_TRACK_LEAKS", props.getProperty("hydra.debug.leaks"))
        buildConfigField("boolean", "DEBUG_ENABLE_REPORTING", props.getProperty("hydra.debug.reporting"))

        // used by Room, to test migrations
        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }


    if (props.getProperty("signing").toBoolean()) {
        signingConfigs {
            create("upload") {
                keyAlias = "upload"
                keyPassword = props.getProperty("keyPassword")
                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    flavorDimensions += "distribution"

    productFlavors {
        // Play Store and officially supported version
        create("store") {
            isDefault = true
            manifestPlaceholders["google_maps_key"] = props.getProperty("mapsApiKey", "")
        }

        create("open") {
            extra["enableCrashlytics"] = false
            versionNameSuffix = "-open"
            applicationIdSuffix = ".open"
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }

    // used by Room, to test migrations
    sourceSets.getByName("test") {
        resources.srcDir(files("$projectDir/schemas"))
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (props.getProperty("signing").toBoolean()) {
                signingConfig = signingConfigs.getByName("upload")
            }
        }

        getByName("debug") {
            // Disable crashlytics in debug builds if necessary.
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
            isDebuggable = true
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        unitTests.all {
            it.systemProperty("robolectric.logging.enabled", "true")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/ASL2.0",
                "META-INF/LICENSE",
                "META-INF/DEPENDENCIES.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/dependencies.txt",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE",
                "META-INF/license.txt",
                "META-INF/LGPL2.1",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE",
                "META-INF/notice.txt"
            )
        }
    }

    lint {
        disable += listOf(
            "RtlSymmetry", "VectorPath", "Overdraw", "GradleDependency", "NotificationPermission", "OldTargetApi", "AndroidGradlePluginVersion"
        )
        showAll = true
        warningsAsErrors = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)

    implementation(libs.androidx.core)
    implementation(libs.androidx.media)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.browser)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.rxjava.rules)
    implementation(libs.okhttp3)
    implementation(libs.moshi)
    implementation(libs.picasso)
    implementation(libs.cachapa)
    implementation(project(":material-intro"))
    implementation(libs.once)
    implementation(libs.materialvalues)
    implementation(libs.insetter)
    implementation(libs.ipcam)

    annotationProcessor(libs.recordbuilder.processor)
    compileOnly(libs.recordbuilder.core)

    // Dependencies for the Play Store version.
    "storeImplementation"(libs.play.maps)
    "storeImplementation"(libs.firebase.analytics)
    "storeImplementation"(libs.firebase.crashlytics)
    "storeImplementation"(libs.play.codescanner)

    // Dependencies for open version.
    "openImplementation"(libs.osmdroid)
    "openImplementation"(libs.zxing)

    if (props.getProperty("hydra.debug.leaks").toBoolean()) {
        logger.info("Leak tracking enabled...")
        debugImplementation(libs.leakcanary)
    }

    testImplementation(libs.junit)
    // Once final classes can be mocked, go back to mockito-core.
    testImplementation(libs.mockito.inline)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.rules)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.espresso.intents)
    testImplementation(libs.androidx.espresso.contrib)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.equalsverifier)
    testImplementation(libs.shazamcrest)
    testImplementation(libs.jsonassert)
    testImplementation(libs.easyrandom)
    testImplementation(libs.commons.lang3)
    testImplementation(libs.commons.validator)
    testImplementation(libs.guava)
}

// Disable the Google Services task for the "open" variants.
googleServices {
    // The open variants do not need this, so allow this to fail.
    missingGoogleServicesStrategy = GoogleServicesPlugin.MissingGoogleServicesStrategy.WARN
}

/**
 * Loads the default properties, and the user properties. This will also load the
 * secret keys.
 */
fun loadProperties(): Properties {
    // Load the default properties.
    val defaultProps = Properties()
    defaultProps.load(FileInputStream(file("config.properties")))

    // Load custom properties if available
    val customProps = Properties(defaultProps)
    val customFile = file("custom-config.properties")
    if (customFile.exists()) {
        customProps.load(FileInputStream(customFile))
    } else {
        logger.info("No custom-config.properties file was found.")
    }

    // Load the secret example secret keys.
    val exampleKeys = Properties(customProps)
    exampleKeys.load(FileInputStream(file("secrets.properties.example")))

    // Load the actual keys if present
    val actualKeys = Properties(exampleKeys)
    val actualKeyFile = file("secrets.properties")
    if (actualKeyFile.exists()) {
        actualKeys.load(FileInputStream(actualKeyFile))
    } else {
        logger.warn("A secrets.properties file was not found.")
    }

    return actualKeys
}

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
