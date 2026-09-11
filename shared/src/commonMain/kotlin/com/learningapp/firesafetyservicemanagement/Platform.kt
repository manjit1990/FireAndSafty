package com.learningapp.firesafetyservicemanagement

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform