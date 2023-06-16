package com.dualcoder.api.router

import com.dualcoder.api.db.PostsDB
import com.dualcoder.api.interceptor.loggerInterceptor
import com.dualcoder.api.interceptor.loginCheckInterceptor
import com.dualcoder.common.KEY_PAGE
import com.dualcoder.common.KEY_TAG
import com.dualcoder.common.KEY_USER
import com.dualcoder.common.KEY_USERNAME
import com.dualcoder.model.cookie.UserCookie
import com.dualcoder.model.dto.PostDto
import com.dualcoder.model.mapper.ref
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlin.math.ceil


fun Route.posts() {
    route("/posts") {
        loggerInterceptor()
        route("", Get) {
            get { list() }
        }

        route("", Post) {
            loginCheckInterceptor()
            post { write() }
        }

        post()
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.list() {
    try {
        val query = call.request.queryParameters
        val page = query[KEY_PAGE]?.toIntOrNull() ?: 1
        if (page < 1) {
            return call.respond(HttpStatusCode.BadRequest)
        }
        val username = query[KEY_USERNAME]
        val tag = query[KEY_TAG]

        val posts = PostsDB.findPosts(username, tag)
        if (posts.isEmpty())
            return call.respond(HttpStatusCode.NoContent, "No Content")

        val lastPage = ceil(posts.size.toDouble() / 10).toString()
        call.response.headers.append(name = "Last-Page", value = lastPage, safeOnly = true)

        call.respond(HttpStatusCode.OK, posts.map { it.shorten() })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.write() {
    try {
        val postDto = call.receive<PostDto>()
        if (postDto.title.isBlank() || postDto.body.isBlank()) {
            return call.respond(HttpStatusCode.BadRequest)
        }

        val userVO = context.attributes.getOrNull<UserCookie>(AttributeKey(KEY_USER))?.toVO()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val postVO = postDto.toVO(userVO.ref())

        val createdPostVO = PostsDB.savePost(postVO) ?: return call.respond(HttpStatusCode.ExpectationFailed)

        call.respond(HttpStatusCode.OK, createdPostVO)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}