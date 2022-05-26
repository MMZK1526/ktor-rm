package mmzk.rm

import io.ktor.client.plugins.contentnegotiation.*
import mmzk.rm.models.RegisterMachine
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun canEncodeEmptyList() = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(RegisterMachine(args = listOf()))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Encode from list: 0", body())
        }
    }
}
