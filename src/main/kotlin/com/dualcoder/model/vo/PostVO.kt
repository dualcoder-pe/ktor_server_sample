package com.dualcoder.model.vo

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

@Serializable
data class PostVO(
    val id: String? = null,
    val title: String,
    val body: String,
    val tags: List<String>,
    val publishedDate: Long,
    val user: UserRef,
) {
    fun shorten() = this.copy(body = Jsoup.parse(body).text())
}