plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.parqlink"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.parqlink"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.inappmessaging)
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.auth0.android:jwtdecode:2.0.1")


    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("com.google.android.gms:play-services-wallet:19.2.1")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.play.services.location)
    implementation(libs.recyclerview)
    implementation ("androidx.recyclerview:recyclerview:1.3.2")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
