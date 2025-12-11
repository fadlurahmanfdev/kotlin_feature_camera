import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")

    id("com.vanniktech.maven.publish") version "0.29.0"
}

android {
    namespace = "com.fadlurahmanfdev.lumi"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val camerax_version = "1.4.1"
    api("androidx.camera:camera-camera2:${camerax_version}")
    api("androidx.camera:camera-lifecycle:${camerax_version}")
    api("androidx.camera:camera-view:${camerax_version}")

//    api("de.hdodenhof:circleimageview:3.1.0")

    api("com.ashampoo:kim:0.20")
}

publishing {
    publications {
        register<MavenPublication>("release"){
            groupId = "com.fadlurahmanfdev"
            artifactId = "lumi"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.fadlurahmanfdev", "lumi", "1.0.0")

    pom {
        name.set("Lumi Camera")
        description.set("Library for handling camera operation such as take a picture, record video, camera streaming, etc")
        inceptionYear.set("2025")
        url.set("https://github.com/fadlurahmanfdev/kotlin_feature_camera/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("fadlurahmanfdev")
                name.set("Taufik Fadlurahman Fajari")
                url.set("https://github.com/fadlurahmanfdev/")
            }
        }
        scm {
            url.set("https://github.com/fadlurahmanfdev/kotlin_feature_camera/")
            connection.set("scm:git:git://github.com/fadlurahmanfdev/kotlin_feature_camera.git")
            developerConnection.set("scm:git:ssh://git@github.com/fadlurahmanfdev/kotlin_feature_camera.git")
        }
    }
}