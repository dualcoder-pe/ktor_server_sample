package com.dualcoder.api.db

import com.dualcoder.common.Log
import com.dualcoder.common.MONGO_COLLECTION_POSTS
import com.dualcoder.model.document.PostDocument
import com.dualcoder.model.vo.PostVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.litote.kmongo.descending
import org.litote.kmongo.eq

object PostsDB {
    suspend fun savePost(postVO: PostVO): PostVO? {
        val postDocument = PostDocument.fromVO(postVO)

        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<PostDocument>(MONGO_COLLECTION_POSTS).insertOne(postDocument)
        }

        Log.d("inserted id : ${result.insertedId?.asObjectId()?.value?.toString()}")
        return if (result.wasAcknowledged()) postVO.copy(id = result.insertedId?.asObjectId()?.value?.toString()) else null
    }

    suspend fun findPosts(username: String?, tag: String?, page: Int = 1, count: Int = 10): List<PostVO> {
        Log.d("findPosts $username $tag $page $count")
        val conditions = mutableMapOf<String, String>()

        username?.let {
            conditions["user.username"] = it
        }

        tag?.let {
            conditions["tags"] = it
        }

        val query = Json.encodeToString(conditions)
        Log.d("query: $query")

        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<PostDocument>(MONGO_COLLECTION_POSTS)
                .find(query)
                .sort(descending(PostDocument::_id))
                .limit(count)
                .skip((page - 1) * count)
        }

        return result.toList().map { it.toVO() }
    }

    suspend fun findPost(id: String): PostVO? {
        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<PostDocument>(MONGO_COLLECTION_POSTS)
                .findOne(PostDocument::_id eq ObjectId(id))
        }

        return result?.toVO()
    }

    suspend fun remove(id: String): Boolean {
        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<PostDocument>(MONGO_COLLECTION_POSTS)
                .deleteOne(PostDocument::_id eq ObjectId(id))
        }
        return result.wasAcknowledged()
    }

    suspend fun update(postVO: PostVO): PostVO? {
        val result = withContext(Dispatchers.IO) {
            MongoDB.database.getCollection<PostDocument>(MONGO_COLLECTION_POSTS)
                .updateOne(PostDocument::_id eq ObjectId(postVO.id), PostDocument.fromVO(postVO))
        }

        return if (result.wasAcknowledged()) postVO else null
    }
}