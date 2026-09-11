import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
            }
        }
    }
}

// compose.experimental {
//     web.application()
// }
