package mmzk.rm.routes

import com.lordcodes.turtle.shellRun
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.DecodeResponse

fun Route.decodeRouting() {
    route("/decode") {
        post {
            try {
                val value = call.receiveText()
                print(value)
                val output = MMZKRM.path?.let {
                    shellRun("./mmzkrm", listOf("-j", "-d", value), it)
                } ?: return@post call.respondText(
                    "Unsupported server OS!", status = HttpStatusCode.InternalServerError
                )
                println(output)

                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            DecodeResponse.serializer(),
                            output
                        ).hasError
                    ) HttpStatusCode.BadRequest else HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText("${e.javaClass.kotlin}: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
