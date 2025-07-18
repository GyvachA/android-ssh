// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinAndroidKsp) apply false
    alias(libs.plugins.androidHilt) apply false
    alias(libs.plugins.serializationKotlin) apply false
    alias(libs.plugins.kotlinCompose) apply false
}
