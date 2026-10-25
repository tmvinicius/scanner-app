plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "br.com.tmvinicius.scanner_app"
    // As bibliotecas Compose/Lifecycle atuais exigem compilar contra a API 37.
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "br.com.tmvinicius.scanner_app"
        minSdk = 24
        // Exigência da Google Play (veja AGENTS.md, seção 4).
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        // Runner que troca o Application pelo HiltTestApplication (app/src/androidTest).
        testInstrumentationRunner = "br.com.tmvinicius.scanner_app.HiltTestRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Schema do Room exportado para app/schemas (versionado no Git, base das migrações).
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// O lint lê o código gerado pelo KSP (inclusive o do androidTest), mas o AGP não declara essa
// dependência. Sem isto, o lint pode rodar enquanto o KSP regenera os arquivos e falhar com
// "Unexpected failure during lint analysis" (FileNotFoundException em build/generated/ksp).
tasks.configureEach {
    val lintVariant = when {
        name.startsWith("lintVitalAnalyze") -> name.removePrefix("lintVitalAnalyze")
        name.startsWith("lintAnalyze") -> name.removePrefix("lintAnalyze")
        else -> return@configureEach
    }.removeSuffix("UnitTest").removeSuffix("AndroidTest")
    dependsOn(tasks.matching { it.name.startsWith("ksp$lintVariant") && it.name.endsWith("Kotlin") })
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    // Rotas tipadas da navegação (@Serializable)
    implementation(libs.kotlinx.serialization.core)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}