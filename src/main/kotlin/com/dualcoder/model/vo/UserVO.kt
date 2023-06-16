package com.dualcoder.model.vo

import com.auth0.jwt.JWT
import com.dualcoder.common.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@kotlinx.serialization.Serializable
data class UserVO(
    val id: String,
    val username: String,
) {
    companion object {
        fun empty(): UserVO = UserVO(id = "", username = "")
    }

    private var _password: String = ""
    val isEmpty: Boolean
        get() = id.isEmpty() && username.isEmpty()
    val password: String
        get() = _password
    val hasPassword: Boolean
        get() = _password.isNotEmpty()

    fun setPassword(password: String) {
        _password = hash(password)
        Log.i("password: $_password")
    }

    fun setHashedPassword(hashedPassword: String) {
        _password = hashedPassword
        Log.i("password: $_password")
    }

    private fun hash(password: String) = Hash.sha256(password)

    fun mask(): Map<String, String> {
        val toMap = mutableMapOf<String, String>()
        toMap[KEY_ID] = id
        toMap[KEY_USERNAME] = username
        return toMap
    }

    fun generateToken(key: String): String {
        val claims = mapOf(KEY_ID to id, KEY_USERNAME to username)
        val algorithm = Enc.algorithm(key)

        return JWT.create()
            .withClaim(KEY_ID, claims[KEY_ID])
            .withClaim(KEY_USERNAME, claims[KEY_USERNAME])
            .withExpiresAt(Date(System.currentTimeMillis() + DAYS_7))
            .sign(algorithm)
    }

    fun checkPassword(password: String): Boolean {
        Log.i("checkPassword: $password => ${hash(password)} $_password")
        return hash(password) == _password
    }
}