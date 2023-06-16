package com.dualcoder.plugins

import com.dualcoder.api.interceptor.jwtInterceptor
import com.dualcoder.api.router.auth
import com.dualcoder.api.router.post
import com.dualcoder.api.router.posts
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        route("/api") {
            jwtInterceptor()
            auth()
            posts()
        }
    }
}
