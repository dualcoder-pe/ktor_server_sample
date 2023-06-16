package com.dualcoder.model.dto

import com.dualcoder.model.vo.PostVO
import com.dualcoder.model.vo.UserRef

@kotlinx.serialization.Serializable
data class PostDto(
    val title: String,
    val body: String,
    val tags: List<String>,
) {
    fun toVO(user: UserRef): PostVO {
        return PostVO(
            title = title,
            body = body,
            tags = tags,
            publishedDate = System.currentTimeMillis(),
            user = user
        )
    }
}