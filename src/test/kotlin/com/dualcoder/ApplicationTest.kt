package com.dualcoder

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.dualcoder.plugins.*
import org.bson.types.ObjectId

class ApplicationTest {

    @Test
    fun tempTest() {
        println(
        ObjectId().toString()
        )
    }

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
