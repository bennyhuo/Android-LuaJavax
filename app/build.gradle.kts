plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        val release by getting {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
        // Or, if you prefer, you can continue to check for errors in release builds,
        // but continue the build even when errors are found:
        isAbortOnError = false
    }
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(project(":luajavax"))
    api("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")

    api("org.slf4j:slf4j-api:1.7.21")
    api("com.github.tony19:logback-android-core:1.1.1-6")
    api("com.github.tony19:logback-android-classic:1.1.1-6") {
        // workaround issue #73
        exclude(group = "com.google.android", module = "android")
    }
}
