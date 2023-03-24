package mmzk.rm.routes

import mmzk.rm.models.EncodeRequest
import com.lordcodes.turtle.shellRun
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.EncodeResponse
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.io.path.writeText

fun Route.encodeRouting() {
    route("/encode") {
        post {
            try {
                val rm = try {
                    call.receive<EncodeRequest>()
                } catch (e: Exception) {
                    return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        EncodeResponse(hasError = true, errors = listOf("$e"))
                    )
                }
                val output = if (rm.code == null) {
                    println(listOf("-j", "-e").plus(rm.args))
                    MMZKRM.path?.let {
                        shellRun(
                            "./mmzkrm",
                            listOf("-j", "-e").plus(rm.args),
                            it
                        )
                    }
                } else {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val output = MMZKRM.path?.let { shellRun("./mmzkrm", listOf("-j", "-e", file.pathString), it) }
                    file.deleteIfExists()
                    output
                } ?: return@post call.respond(
                    status = HttpStatusCode.BadRequest,
                    EncodeResponse(hasError = true, errors = listOf("Unsupported server OS!"))
                )
                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            EncodeResponse.serializer(),
                            output
                        ).hasError
                    ) HttpStatusCode.BadRequest else HttpStatusCode.OK
                )
            } catch (e: Exception) {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    EncodeResponse(hasError = true, errors = listOf("$e"))
                )
            }
        }
    }
}
