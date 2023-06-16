package com.dualcoder.api.router

import com.dualcoder.api.db.UserDB
import com.dualcoder.common.*
import com.dualcoder.model.cookie.UserCookie
import com.dualcoder.model.dto.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import javax.naming.AuthenticationException

fun Route.auth() {
    route("/auth") {
        post("/register") { register() }
        post("/login") { login() }
        get("/check") { check() }
        post("/logout") { logout() }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.register() {
    val userDto = call.receive<UserDto>()

    try {
        val exists = UserDB.findUserByUsername(userDto.username) != null
        if (exists) {
            return call.respond(HttpStatusCode.Conflict)
        }

        val userVO = UserDB.saveUser(userDto.toVO()) ?: return call.respond(HttpStatusCode.ExpectationFailed)

        call.response.cookies.append(
            name = KEY_ACCESS_TOKEN,
            value = userVO.generateToken(jwtSecret()),
            maxAge = DAYS_7,
            httpOnly = true,
            path = "/",
        )

        call.respond(HttpStatusCode.OK, userVO.mask())
    } catch (e: AuthenticationException) {
        return call.respond(HttpStatusCode.Unauthorized)
    } catch (e: Exception) {
        Log.e(e.localizedMessage)
        return call.respond(HttpStatusCode.InternalServerError)
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.login() {
    val userDto = call.receive<UserDto>()
    if (userDto.username.isBlank() || userDto.password.isBlank()) {
        return call.respond(HttpStatusCode.Unauthorized)
    }

    val userVO = UserDB.findUser(userDto.username, userDto.password) ?: return call.respond(HttpStatusCode.Unauthorized)

    call.response.cookies.append(
        name = KEY_ACCESS_TOKEN,
        value = userVO.generateToken(jwtSecret()),
        maxAge = DAYS_7,
        httpOnly = true,
        path = "/",
    )

    call.respond(HttpStatusCode.OK, userVO.mask())
}

suspend fun PipelineContext<Unit, ApplicationCall>.check() {
    if (!call.attributes.contains(AttributeKey<UserCookie>(KEY_USER))) {
        return call.respond(HttpStatusCode.Unauthorized)
    }
    call.respond(HttpStatusCode.OK, call.attributes[AttributeKey<UserCookie>(KEY_USER)])
}

suspend fun PipelineContext<Unit, ApplicationCall>.logout() {
    call.response.cookies.append(name = KEY_ACCESS_TOKEN, value = "", path = "/")
    call.respond(HttpStatusCode.NoContent)
}