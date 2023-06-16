package com.dualcoder.api.interceptor

import com.dualcoder.common.Log
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.loggerInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        Log.v("[Logger] ${call.request.httpMethod} ${call.request.uri} ${call.request.queryParameters.entries()}")
        proceed()
    }
}