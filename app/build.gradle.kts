import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// =========================
//   Version configuration
// =========================

val major = 0
val minor = 1
val patch = 1
val build = 0

val baseVersionName = "$major.$minor.$patch Build $build"

val versionCodeBase =
    (String.format("%02d", major) +
            String.format("%02d", minor) +
            String.format("%02d", patch) +
            String.format("%02d", build)).toInt()

// =========================
//   Android configuration
// =========================

extensions.configure<ApplicationExtension>("android") {

    namespace = "com.github.codeworkscreativehub.borderbound"

    compileSdk = 37

    defaultConfig {
        minSdk = 28
        targetSdk = 37
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
            applicationIdSuffix = ".debug"

            resValue("string", "app_version", baseVersionName)
            resValue("string", "app_name", "Border Bound Debug")
            buildConfigField("boolean", "DEBUG_LEVELS", "true")
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

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
        resValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
    }

    packaging {
        jniLibs {
            keepDebugSymbols.add("libandroidx.graphics.path.so")
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

// =========================
//   Kotlin
// =========================

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        )
    }
}

// =========================
//   Compress level files
// =========================

tasks.register("compressLevelFile") {
    description = "Compress level files for release builds"
    doLast {
        compress(
            rootProject
                .file("app/src/main/assets/levelsEasy.xml")
                .absolutePath
        )

        compress(
            rootProject
                .file("app/src/main/assets/levelsMedium.xml")
                .absolutePath
        )

        compress(
            rootProject
                .file("app/src/main/assets/levelsHard.xml")
                .absolutePath
        )

        compress(
            rootProject
                .file("app/src/main/assets/levelsCommunity.xml")
                .absolutePath
        )
    }
}

// Make preBuild depend on compression
tasks.named("preBuild") {
    dependsOn("compressLevelFile")
}

// =========================
//   Delete compressed files
// =========================

tasks.register("deleteCompressedLevelFiles") {
    description = "Delete compressed level files"
    doLast {
        project.delete(
            rootProject.fileTree(
                "app/src/main/assets"
            ) {
                include("**/*.compressed")
            }
        )
    }
}

// Clean up compressed files after assemble tasks
tasks.matching {
    it.name.startsWith("assemble")
}.configureEach {
    finalizedBy("deleteCompressedLevelFiles")
}

// =========================
//   APK naming
// =========================

tasks.matching {
    it.name.startsWith("assemble") &&
            it.name.endsWith("Release")
}.configureEach {

    doLast {
        val flavor = when {
            name.contains("Prod", ignoreCase = true) -> "prod"
            name.contains("Beta", ignoreCase = true) -> "beta"
            name.contains("Alpha", ignoreCase = true) -> "alpha"
            name.contains("Nightly", ignoreCase = true) -> "nightly"
            else -> return@doLast
        }

        val apkDir = layout.buildDirectory
            .dir("outputs/apk/release")
            .get()
            .asFile

        if (apkDir.exists()) {
            apkDir.walkTopDown()
                .filter {
                    it.isFile &&
                            it.extension.equals("apk", ignoreCase = true)
                }
                .forEach { apk ->

                    val target = File(
                        apk.parentFile,
                        "app_${flavor}_release.apk"
                    )

                    if (apk.absolutePath != target.absolutePath) {
                        if (target.exists()) {
                            target.delete()
                        }

                        if (apk.renameTo(target)) {
                            println(
                                "Renamed APK: ${target.name}"
                            )
                        } else {
                            println(
                                "Warning: Failed to rename ${apk.name}"
                            )
                        }
                    }
                }
        }
    }
}

// =========================
//   Functions
// =========================

fun compress(path: String) {
    val file = rootProject.file(path)

    if (!file.exists()) {
        error("Level file not found: ${file.absolutePath}")
    }

    var levels = file.readText()

    println(
        "Compressing ${file.name}"
    )
    println(
        "  Original: ${levels.length} bytes"
    )

    levels = levels
        .replace(Regex("\\s+"), " ")
        .replace(Regex("\"\\n ?"), "\" ")
        .replace(Regex(" ?\\n ?"), "")
        .replace(Regex("=\" "), "=\"")
        .replace(Regex("<!--([^>]*)-->"), "")
        .plus("\n")

    println(
        "  Compressed: ${levels.length} bytes"
    )

    rootProject
        .file("${path}.compressed")
        .writeText(levels)
}

// =========================
//   Dependencies
// =========================

dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to rootProject
                    .file("app/libs")
                    .absolutePath,
                "include" to listOf("*.jar")
            )
        )
    )

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
    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}