package com.dualcoder.common

import java.security.MessageDigest

class Hash {
    companion object {
        fun sha256(input: String): String {
            val bytes = input.toByteArray(Charsets.UTF_8)
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}