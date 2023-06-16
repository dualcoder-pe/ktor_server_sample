package com.dualcoder.plugins

import com.dualcoder.api.db.MongoDB
import io.ktor.server.application.*

fun Application.configureDB() {
    MongoDB.connectString = "mongodb://localhost:27017"
}