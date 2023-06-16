package com.dualcoder.common

import io.ktor.util.logging.*

private val logger = KtorSimpleLogger("com.dualcoder.ktor_server_sample")

object Log : Logger by logger {
    fun v(msg: String) {
        logger.trace(msg)
    }

    fun d(msg: String) {
        logger.debug(msg)
    }

    fun i(msg: String) {
        logger.info(msg)
    }

    fun w(msg: String) {
        logger.warn(msg)
    }

    fun e(msg: String) {
        logger.error(msg)
    }
}