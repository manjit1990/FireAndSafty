package com.learningapp.firesafetyservicemanagement

class WasmJsPlatform : Platform {
    override val name: String = "WebAssembly/JS"
}

actual fun getPlatform(): Platform = WasmJsPlatform()
