package com.dualcoder.model.cookie

import com.dualcoder.model.vo.UserVO

@kotlinx.serialization.Serializable
data class UserCookie(
    val id: String,
    val username: String
) {
    fun toVO() = UserVO(id, username)
}