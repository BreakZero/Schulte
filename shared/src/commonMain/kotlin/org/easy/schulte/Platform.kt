package org.easy.schulte

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform