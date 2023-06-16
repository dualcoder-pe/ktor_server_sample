package com.dualcoder

import com.dualcoder.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureStatic()
    configureSerialization()
    configureDB()
    configureRouting()
}
