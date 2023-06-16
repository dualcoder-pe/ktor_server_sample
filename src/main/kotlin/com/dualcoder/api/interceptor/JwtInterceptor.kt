package com.dualcoder.api.interceptor

import com.auth0.jwt.JWT
import com.dualcoder.common.*
import com.dualcoder.model.cookie.UserCookie
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.util.*

fun Route.jwtInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        Log.v("jwtInterceptor ${call.request.cookies.rawCookies.size}")

        val token = call.request.cookies[KEY_ACCESS_TOKEN] ?: return@intercept proceed()

        try {
            val algorithm = Enc.algorithm(jwtSecret())
            val verifier = JWT.require(algorithm).build()
            val decoded = verifier.verify(token)

            val userCookie =
                UserCookie(decoded.claims[KEY_ID]?.asString() ?: "", decoded.claims[KEY_USERNAME]?.asString() ?: "")

            context.attributes.put(AttributeKey(KEY_USER), userCookie)

            val now = System.currentTimeMillis() / 1000
            if ((decoded.expiresAt?.time ?: 0) - now < DAYS_3) {
                val newToken = JWT.create()
                    .withClaim(KEY_ID, userCookie.id)
                    .withClaim(KEY_USERNAME, userCookie.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + DAYS_7))
                    .sign(algorithm)

                call.response.cookies.append(
                    name = KEY_ACCESS_TOKEN,
                    value = newToken,
                    maxAge = DAYS_7,
                    httpOnly = true,
                    path = "/",
                )
            }
        } catch (e: Exception) {
            Log.d("Not logged in $e")
        } finally {
            proceed()
        }
    }
}