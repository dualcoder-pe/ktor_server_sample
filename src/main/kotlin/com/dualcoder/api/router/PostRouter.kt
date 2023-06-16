package com.dualcoder.api.router

import com.dualcoder.api.db.PostsDB
import com.dualcoder.api.interceptor.checkOwnPostInterceptor
import com.dualcoder.api.interceptor.getPostByIdInterceptor
import com.dualcoder.api.interceptor.loggerInterceptor
import com.dualcoder.api.interceptor.loginCheckInterceptor
import com.dualcoder.common.KEY_ID
import com.dualcoder.common.KEY_POST
import com.dualcoder.model.dto.PostDto
import com.dualcoder.model.vo.PostVO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

fun Route.post() {
    route("/{id}") {
        loggerInterceptor()
        getPostByIdInterceptor()

        route("", HttpMethod.Get) {
            get { read() }
        }

        route("", HttpMethod.Delete) {
            loginCheckInterceptor()
            checkOwnPostInterceptor()
            delete { remove() }
        }

        route("", HttpMethod.Patch) {
            loginCheckInterceptor()
            checkOwnPostInterceptor()
            patch { update() }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.read() {
    call.respond(HttpStatusCode.OK, call.attributes.get<PostVO>(AttributeKey(KEY_POST)))
}

suspend fun PipelineContext<Unit, ApplicationCall>.remove() {
    val id = call.parameters[KEY_ID].toString()

    if (PostsDB.remove(id)) call.respond(HttpStatusCode.NoContent)
    else call.respond(HttpStatusCode.InternalServerError)
}

suspend fun PipelineContext<Unit, ApplicationCall>.update() {
    try {
        val postDto = call.receive<PostDto>()
        if (postDto.title.isBlank() || postDto.body.isBlank()) {
            return call.respond(HttpStatusCode.BadRequest)
        }

        val postVO = context.attributes.get<PostVO>(AttributeKey(KEY_POST)).copy(
            title = postDto.title,
            body = postDto.body,
            tags = postDto.tags
        )

        PostsDB.update(postVO) ?: return call.respond(HttpStatusCode.ExpectationFailed)

        call.respond(HttpStatusCode.OK, postVO)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}