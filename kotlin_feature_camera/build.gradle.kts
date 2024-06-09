plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "co.id.fadlurahmanfdev.kotlin_feature_camera"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val camerax_version = "1.3.3"
    api("androidx.camera:camera-camera2:${camerax_version}")
    api("androidx.camera:camera-lifecycle:${camerax_version}")
    api("androidx.camera:camera-view:${camerax_version}")
}

publishing {
    publications {
        register<MavenPublication>("release"){
            groupId = "co.id.fadlurahmanfdev"
            artifactId = "kotlin_feature_camera"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}