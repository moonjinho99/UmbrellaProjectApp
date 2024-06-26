plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.umbrella"
    compileSdk = 34
    buildToolsVersion = "34.0.0" // 변경된 부분

    defaultConfig {
        applicationId = "com.example.umbrella"
        minSdk = 24
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

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.0") // 수정된 부분
    implementation("androidx.constraintlayout:constraintlayout:2.1.3") // 최신 버전으로 업데이트
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("androidx.navigation:navigation-fragment:2.3.5")
    implementation("androidx.navigation:navigation-ui:2.3.5")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bootpay:client_android_java:3.5.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation(fileTree(mapOf(
        "dir" to "src\\main\\jniLibs",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))

    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.0.0")
    implementation(files("libs\\libDaumMapAndroid.jar"))
    implementation(files("libs\\libDaumMapAndroid.jar"))
    implementation(files("libs\\libDaumMapAndroid.jar"))
    implementation(files("libs\\libDaumMapAndroid.jar"))
    implementation(files("libs\\libDaumMapAndroid.jar"))


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}