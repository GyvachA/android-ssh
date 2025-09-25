import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.serializationKotlin)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.detekt)
}

detekt {
    toolVersion = libs.versions.detekt.asProvider().get()
    parallel = true
    config.setFrom(files("${rootDir}/config/detekt/detekt.yml"))
    allRules = false
    buildUponDefaultConfig = true
    autoCorrect = true
}

val localProps = gradleLocalProperties(rootDir, providers)

android {
    namespace = "com.gyvacha.androidssh"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gyvacha.androidssh"
        minSdk = 26
        targetSdk = 36
        versionCode = 9
        versionName = "1.1.3"
        val yandexAdBannerId = localProps["YANDEX_AD_BANNER_ID"] ?: ""
        buildConfigField("String", "YANDEX_AD_BANNER_ID", "\"${yandexAdBannerId}\"")
        val yandexAdBannerIdSecond = localProps["YANDEX_AD_BANNER_ID_SECOND"] ?: ""
        buildConfigField("String", "YANDEX_AD_BANNER_ID_SECOND", "\"${yandexAdBannerIdSecond}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    splits {
        abi {
            isEnable = false
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("github") {
            storeFile = localProps["GITHUB_STORE_FILE"]?.let { rootProject.file(it) }
            storePassword = localProps["GITHUB_STORE_PASSWORD"] as String?
            keyAlias = localProps["GITHUB_KEY_ALIAS"] as String?
            keyPassword = localProps["GITHUB_KEY_PASSWORD"] as String?
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                debugSymbolLevel = "NONE"
            }
        }

        create("releaseGithub") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("github")
        }

        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree("libs"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.database.sqlcipher)
    implementation(libs.jsch)
    implementation(libs.bouncycastle.crypto)
    implementation(libs.bouncycastle.pkix)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.crypto.tink)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.camera)
    implementation(libs.camera.core)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.yandex.ads)
    implementation(libs.androidx.recyclerview)
    ksp(libs.room.compiler)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(18)
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
