package com.dualcoder.api.interceptor

import com.dualcoder.api.db.PostsDB
import com.dualcoder.common.KEY_ID
import com.dualcoder.common.KEY_POST
import com.dualcoder.common.KEY_USER
import com.dualcoder.common.Log
import com.dualcoder.model.cookie.UserCookie
import com.dualcoder.model.vo.PostVO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.bson.types.ObjectId

fun Route.getPostByIdInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        val id = call.parameters[KEY_ID].toString()
        Log.v("call GetPostByIdInterceptor $id")

        if (!ObjectId.isValid(id)) {
            return@intercept call.respond(HttpStatusCode.BadRequest)
        }

        val postVO = PostsDB.findPost(id) ?: return@intercept call.respond(HttpStatusCode.NotFound)
        call.attributes.put(AttributeKey(KEY_POST), postVO)

        proceed()
    }
}

fun Route.checkOwnPostInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        Log.v("call CheckOwnPostInterceptor")

        if (!call.attributes.contains(AttributeKey<PostVO>(KEY_POST))
            || !call.attributes.contains(AttributeKey<UserCookie>(KEY_USER))
            || !call.attributes.get<PostVO>(AttributeKey(KEY_POST)).user.id.toHexString()
                .equals(call.attributes.get<UserCookie>(AttributeKey(KEY_USER)).id)
        ) {
            return@intercept call.respond(HttpStatusCode.Forbidden)
        }
        proceed()
    }
}