plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.mapbox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mapbox"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

   // implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependencia de core-ktx
    implementation("androidx.core:core-ktx:1.13.0")
    //MapBox dependencias
   // implementation ("com.mapbox.maps:android:10.15.0")
    implementation("com.mapbox.maps:android:11.8.0")
   // implementation ("com.mapbox.maps:plugin-annotation:11.8.0") // Para manejar los marcadores



    //implementation ("com.mapbox.maps:android:10.13.1")
    //implementation ("com.mapbox.maps:plugin-annotation:10.13.1")

}