package com.dualcoder.model.document

import com.dualcoder.model.vo.PostVO
import com.dualcoder.model.vo.UserRef
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist


@Serializable
data class PostDocument(
    @Contextual val _id: ObjectId = ObjectId.get(),
    val title: String,
    val body: String,
    val tags: List<String>,
    val publishedDate: Long,
    val user: UserRef,
) {
    companion object {
        fun fromVO(postVO: PostVO): PostDocument =
            PostDocument(
                title = postVO.title,
                body = Jsoup.clean(postVO.body, Safelist.relaxed()),
                tags = postVO.tags,
                publishedDate = postVO.publishedDate,
                user = postVO.user,
            )
    }

    fun toVO() = PostVO(
        id = _id.toHexString(),
        title = title,
        body = body,
        tags = tags,
        publishedDate = publishedDate,
        user = user,
    )
}