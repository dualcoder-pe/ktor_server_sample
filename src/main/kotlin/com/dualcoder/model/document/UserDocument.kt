package com.dualcoder.model.document

import com.dualcoder.model.vo.UserVO
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class UserDocument(
    @Contextual val _id: ObjectId = ObjectId.get(),
    val username: String,
    val hashedPassword: String
) {
    companion object {
        fun fromVO(userVO: UserVO): UserDocument =
            UserDocument(username = userVO.username, hashedPassword = userVO.password)
    }

    fun toVO(): UserVO =
        UserVO(id = _id.toHexString(), username = username).also { it.setHashedPassword(hashedPassword) }
}
