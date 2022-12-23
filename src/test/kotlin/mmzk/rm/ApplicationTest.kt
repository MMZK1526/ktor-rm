package mmzk.rm

import io.ktor.client.plugins.contentnegotiation.*
import mmzk.rm.models.EncodeRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.DecodeResponse
import mmzk.rm.models.EncodeNum
import mmzk.rm.models.EncodeResponse
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun canEncodeEmptyList() = testEncodeList(listOf(), EncodeNum(false, "0"))

    @Test
    fun canEncodeSingletonList() = testEncodeList(listOf(10), EncodeNum(false, "1024"))

    @Test
    fun canEncodeList() = testEncodeList(listOf(3, 5, 2), EncodeNum(false, "4616"))

    @Test
    fun doNotEncodeBigList() = testEncodeList(listOf(114514, 1919810), EncodeNum(true))

    @Test
    fun canEncodeRM() = testEncodeRM(
        """
            1- 1 2
            0+ 0
        """.trimIndent(),
        listOf(EncodeNum(false, "152"), EncodeNum(false, "1")),
        EncodeNum(false, "28544953854119197621165719388989902727654932480")
    )

    @Test
    fun doNotEncodeLargeRM() = testEncodeRM(
        """
            1- 1 6
            2- 2 4
            3+ 3
            0+ 1
            3- 5 0
            2+ 4
            H
        """.trimIndent(),
        listOf(
            EncodeNum(false, "408"),
            EncodeNum(false, "2272"),
            EncodeNum(false, "448"),
            EncodeNum(false, "3"),
            EncodeNum(false, "8064"),
            EncodeNum(false, "144"),
            EncodeNum(false, "0")
        ),
        EncodeNum(true)
    )

    @Test
    fun canDetectEncodeSyntaxError() = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(code = "FOOBAR"))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertTrue(data.hasError)
        }
    }

    @Test
    fun canDetectDecodeSyntaxError() = testApplication {
        val client = createClient {}
        client.post("/decode") {
            contentType(ContentType.Application.Json)
            setBody("-1919810")
        }.apply {
//            assertEquals(HttpStatusCode.BadRequest, status)
            val data = Json.decodeFromString(DecodeResponse.serializer(), bodyAsText())
            assertTrue(data.hasError)
        }
    }

    private fun testEncodeList(list: List<Int>, expected: EncodeNum) = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(args = list))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertFalse(data.hasError)
            assertEquals(expected, data.encodeFromList)
        }
    }

    private fun testEncodeRM(code: String, expectedList: List<EncodeNum>, expectedGodel: EncodeNum) = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(code = code))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertFalse(data.hasError)
            assertEquals(data.encodeFromRM, expectedGodel)
            assertEquals(data.encodeToLine, expectedList)
        }
    }
}
