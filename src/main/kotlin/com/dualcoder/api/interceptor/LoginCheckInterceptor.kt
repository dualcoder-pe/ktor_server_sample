package com.dualcoder.api.interceptor

import com.dualcoder.common.KEY_USER
import com.dualcoder.common.Log
import com.dualcoder.model.cookie.UserCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Route.loginCheckInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        Log.v("call LoginCheckInterceptor")

        call.attributes.allKeys.forEach { key ->
            Log.v("$key => ${call.attributes[key]}")
        }

        if (!call.attributes.contains(AttributeKey<UserCookie>(KEY_USER))) {
            return@intercept call.respond(HttpStatusCode.Unauthorized)
        }
        Log.v("Logged In")
        proceed()
    }
}