package mmzk.rm.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.DecodeResponse

fun Route.decodeRouting() {
    route("/decode") {
        put {
            if (MMZKRM.path == null) {
                return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    DecodeResponse(hasError = true, errors = listOf("Unsupported Server OS!"))
                )
            }
            try {
                val value = call.receiveText()
                val output = try {
                    MMZKRM.run(listOf("-j", "-d", value))
                } catch (e: Exception) {
                    return@put call.respond(
                        status = HttpStatusCode.InternalServerError,
                        DecodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                    )
                } ?: return@put call.respond(
                    status = HttpStatusCode.RequestTimeout,
                    DecodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                )
                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            DecodeResponse.serializer(),
                            output
                        ).hasError
                    ) HttpStatusCode.BadRequest else HttpStatusCode.OK
                )
            } catch (e: Exception) {
                return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    DecodeResponse(hasError = true, errors = listOf("$e"))
                )
            }
        }
        post {
            if (MMZKRM.path == null) {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    DecodeResponse(hasError = true, errors = listOf("Unsupported Server OS!"))
                )
            }
            try {
                val value = call.receiveText()
                val output = try {
                    MMZKRM.run(listOf("-j", "-d", value))
                } catch (e: Exception) {
                    return@post call.respond(
                        status = HttpStatusCode.InternalServerError,
                        DecodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                    )
                } ?: return@post call.respond(
                    status = HttpStatusCode.RequestTimeout,
                    DecodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                )
                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            DecodeResponse.serializer(),
                            output
                        ).hasError
                    ) HttpStatusCode.BadRequest else HttpStatusCode.OK
                )
            } catch (e: Exception) {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    DecodeResponse(hasError = true, errors = listOf("$e"))
                )
            }
        }
    }
}
