plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// =========================
//   Version configuration
// =========================

val major = 0
val minor = 1
val patch = 0
val build = 2

val baseVersionName = "$major.$minor.$patch Build $build"

val versionCodeBase =
    (String.format("%02d", major) +
            String.format("%02d", minor) +
            String.format("%02d", patch) +
            String.format("%02d", build)).toInt()

// =========================
//   Android configuration
// =========================

android {
    namespace = "com.github.codeworkscreativehub.borderbound"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28
        targetSdk = 36
        versionCode = versionCodeBase
        versionName = baseVersionName
        buildConfigField("boolean", "DEBUG_LEVELS", "false")
    }

    flavorDimensions += "channel"

    productFlavors {
        create("prod") {
            dimension = "channel"
            applicationId = "app.borderbound"
            resValue("string", "app_name", "Border Bound")
        }

        create("beta") {
            dimension = "channel"
            applicationId = "app.borderbound.beta"
            versionNameSuffix = "-beta"
            resValue("string", "app_name", "Border Bound Beta")
        }

        create("alpha") {
            dimension = "channel"
            applicationId = "app.borderbound.alpha"
            versionNameSuffix = "-alpha"
            resValue("string", "app_name", "Border Bound Alpha")
        }

        create("nightly") {
            dimension = "channel"
            applicationId = "app.borderbound.nightly"
            versionNameSuffix = "-nightly"
            resValue("string", "app_name", "Border Bound Nightly")
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"

            resValue("string", "app_version", baseVersionName)
            resValue("string", "app_name", "Border Bound Debug")
            resValue("string", "empty", "")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_version", baseVersionName)
            resValue("string", "empty", "")
        }
    }

    applicationVariants.all {
        val flavorName = this.flavorName

        outputs.all {
            val output =
                this as com.android.build.gradle.internal.api.BaseVariantOutputImpl

            output.outputFileName =
                "app_${flavorName}_release.apk"
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

// =========================
//   Kotlin
// =========================

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// =========================
//   Functions
// =========================

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

// =========================
//   Dependencies
// =========================

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