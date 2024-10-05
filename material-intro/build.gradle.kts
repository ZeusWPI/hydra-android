/*
 * Copyright (c) 2017 Jan Heinrich Reimer
 * Copyright (c) 2024 Niko Strijbol
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
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
}

val versions = loadAndroidVersions()

android {
    namespace = "com.heinrichreimersoftware.materialintro"

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
        targetSdk = 34
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
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