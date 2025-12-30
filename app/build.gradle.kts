plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Top of build.gradle.kts
val major = 0
val minor = 1
val patch = 0
val build = 0

val type = 0 // 1=beta, 2=alpha else=production

val baseVersionName = "$major.$minor.$patch"

val versionCodeInt =
    (String.format("%02d", major) + String.format("%02d", minor) + String.format(
        "%02d",
        patch
    ) + String.format("%02d", build)).toInt()

val versionNameStr = when (type) {
    1 -> "$baseVersionName-beta build $build"
    2 -> "$baseVersionName-alpha build $build"
    else -> "$baseVersionName build $build"
}

val applicationName = when (type) {
    1 -> "app.borderbound.beta"
    2 -> "app.borderbound.alpha"
    else -> "app.borderbound"
}

android {
    namespace = "com.github.codeworkscreativehub.borderbound"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = applicationName
        minSdk = 28
        targetSdk = 36
        versionCode = versionCodeInt
        versionName = versionNameStr
        buildConfigField("boolean", "DEBUG_LEVELS", "false")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".dev"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "BorderBound Dev")
            resValue("string", "app_version", versionNameStr)
            resValue("string", "empty", "")
            buildConfigField("boolean", "DEBUG_LEVELS", "true")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "BorderBound")
            resValue("string", "app_version", versionNameStr)
            resValue("string", "empty", "")
        }
    }

    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
                if (output?.outputFileName?.endsWith(".apk") == true) {
                    output.outputFileName =
                        "${defaultConfig.applicationId}_v${defaultConfig.versionName}-Signed.apk"
                }
            }
        }
        if (buildType.name == "debug") {
            outputs.all {
                val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
                if (output?.outputFileName?.endsWith(".apk") == true) {
                    output.outputFileName =
                        "${defaultConfig.applicationId}_v${defaultConfig.versionName}-Debug.apk"
                }
            }
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
    }

    packaging {
        // Keep debug symbols for specific native libraries
        // found in /app/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib
        jniLibs {
            keepDebugSymbols.add("libandroidx.graphics.path.so") // Ensure debug symbols are kept
        }
    }

    tasks.register("compressLevelFile") {
        doLast {
            compress("$projectDir/src/main/assets/levelsEasy.xml")
            compress("$projectDir/src/main/assets/levelsMedium.xml")
            compress("$projectDir/src/main/assets/levelsHard.xml")
            compress("$projectDir/src/main/assets/levelsCommunity.xml")
        }
    }

    // Make preBuild depend on this task
    tasks.named("preBuild") {
        dependsOn("compressLevelFile")
    }

    tasks.register("deleteCompressedLevelFiles") {
        doLast {
            project.delete(
                project.fileTree("$projectDir/src/main/assets") {
                    include("**/*.compressed")
                }
            )
        }
    }

    tasks.matching { it.name.startsWith("assemble") }.configureEach {
        finalizedBy("deleteCompressedLevelFiles")
    }

}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

fun compress(path: String) {
    val file = file(path)
    var levels = file.readText()
    println("  Original: ${levels.length} bytes")

    // Apply the same regex replacements
    levels = levels
        .replace(Regex("\\s+"), " ")
        .replace(Regex("\"\\n ?"), "\" ")
        .replace(Regex(" ?\\n ?"), "")
        .replace(Regex("=\" "), "=\"")
        .replace(Regex("<!--([^>]*)-->"), "")
        .plus("\n")

    println(", compressed: ${levels.length} bytes")

    file("$path.compressed").writeText(levels)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.graphics.core)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}