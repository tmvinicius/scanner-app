// Arquivo de build raiz: apenas declara os plugins usados pelo módulo app.
// As versões ficam todas em gradle/libs.versions.toml.
buildscript {
    dependencies {
        // O AGP 9 traz o Kotlin Gradle Plugin 2.2.10 embutido; esta linha o eleva para a
        // versão do catálogo, alinhada com o plugin do compilador Compose e com o KSP.
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}