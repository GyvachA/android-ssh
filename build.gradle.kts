plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinAndroidKsp) apply false
    alias(libs.plugins.androidHilt) apply false
    alias(libs.plugins.serializationKotlin) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.googleServicesPlugin) apply false
    alias(libs.plugins.firebaseCrashlyticsPlugin) apply false
}
