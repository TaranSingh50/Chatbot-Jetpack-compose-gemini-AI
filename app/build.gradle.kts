import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("jacoco")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

// Fetch API key as a string and ensure it's correctly formatted
val googleApiKey = localProperties["GOOGLE_API_KEY"] as String? ?: "DEFAULT_API_KEY"

android {
    signingConfigs {
        create("release") {
            storeFile =
                file("/home/techies/TaranWork/2025/JetpackCompose/AIChatbotWithCompose/AIChat.jks")
            storePassword = project.findProperty("STORE_PASSWORD") as String
            keyAlias = project.findProperty("ALIAS_KEY") as String
            keyPassword = project.findProperty("ALIAS_KEY_PASSWORD") as String
        }
    }
    namespace = "it.techies.aichatbotwithcompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.techies.aichatbotwithcompose"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GOOGLE_API_KEY", "\"$googleApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            enableUnitTestCoverage = true // ✅ Enable coverage for debug build
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sonarqube {
        properties {
            property("sonar.projectName", "MyKotlinApp")
            property("sonar.projectKey", "MyKotlinApp")
            property("sonar.language", "kotlin")
            property("sonar.sourceEncoding", "UTF-8")
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.generativeai)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("com.airbnb.android:lottie-compose:6.6.3")
    implementation(libs.androidx.core.splashscreen)

    // JUnit 5 API and Test Engine
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")

    // JUnit Vintage (for JUnit 4 tests compatibility)
    testImplementation("org.junit.vintage:junit-vintage-engine:5.12.0")

    // Test Runtime (ensures the test engine is included)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")

    // Mockito for mocking dependencies (optional)
    testImplementation("org.mockito:mockito-core:5.16.0")

    // Jacoco support for JUnit
    testImplementation("org.jacoco:org.jacoco.core:0.8.11")
    testImplementation("junit:junit:4.13.2")

    // Encrypted SharedPreferences
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "Verification" // ✅ Group under 'Verification' tasks
    description = "Generates a Jacoco coverage report for unit tests." // ✅ Description
    dependsOn("testDebugUnitTest") // ✅ Ensure test runs before coverage report

    reports {
        xml.required.set(true)  // ✅ Required for SonarQube
        html.required.set(true) // ✅ Human-readable report
        csv.required.set(false)
    }

    sourceDirectories.setFrom(files("$projectDir/src/main/java/it/techies/aichatbotwithcompose"))
    classDirectories.setFrom(files(layout.buildDirectory.dir("tmp/kotlin-classes/debug"))) // ✅ Updated
    executionData.setFrom(files(layout.buildDirectory.file("jacoco/testDebugUnitTest.exec"))) // ✅ Updated
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // Run Jacoco after tests
}

