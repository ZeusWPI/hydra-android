plugins {
    groovy
    idea
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(libs.android.build.tool)
    implementation(libs.jetbrains.annotations)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
}

tasks.validatePlugins {
    failOnWarning = true
}
