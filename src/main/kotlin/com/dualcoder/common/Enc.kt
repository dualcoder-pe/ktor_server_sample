package com.dualcoder.common

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

class Enc {
    companion object {
        fun algorithm(key: String) = Algorithm.HMAC256(key)
    }
}
fun PipelineContext<Unit, ApplicationCall>.jwtSecret() =
    application.jwtSecret()

fun Application.jwtSecret() = environment.config.propertyOrNull(KEY_JWT_SECRET)?.getString() ?: DEFAULT_SECRET


