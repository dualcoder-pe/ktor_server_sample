package com.dualcoder.model.dto

import com.dualcoder.model.vo.UserVO

@kotlinx.serialization.Serializable
data class UserDto(
    val username: String,
    val password: String,
) {
    fun toVO(): UserVO = UserVO("", username).also { it.setPassword(password) }
}