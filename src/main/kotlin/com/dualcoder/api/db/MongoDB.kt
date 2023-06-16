package com.dualcoder.api.db

import com.dualcoder.common.MONGO_DATABASE
import com.mongodb.ConnectionString
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoDB {
    var connectString: String = ""
    private val client: CoroutineClient by lazy { KMongo.createClient(ConnectionString(connectString)).coroutine }
    val database by lazy { client.getDatabase(MONGO_DATABASE) }
}