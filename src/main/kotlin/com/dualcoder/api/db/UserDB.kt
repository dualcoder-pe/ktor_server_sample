package com.dualcoder.api.db

import com.dualcoder.common.MONGO_COLLECTION_USERS
import com.dualcoder.model.document.UserDocument
import com.dualcoder.model.vo.UserVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.eq

object UserDB {
    suspend fun findUserByUsername(username: String): UserVO? {
        return withContext(Dispatchers.IO) {
            val collection = MongoDB.database.getCollection<UserDocument>(MONGO_COLLECTION_USERS)
            collection.find(UserDocument::username eq username).first()?.toVO()
        }
    }

    suspend fun saveUser(userVO: UserVO): UserVO? {
        val userDocument = UserDocument.fromVO(userVO)

        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<UserDocument>(MONGO_COLLECTION_USERS).insertOne(userDocument)
        }

        return if (result.wasAcknowledged()) userDocument.toVO() else null
    }

    suspend fun findUser(username: String, password: String): UserVO? {
        val userVO = findUserByUsername(username) ?: return null

        return userVO.let {
            if (it.checkPassword(password)) it
            else null
        }
    }
}