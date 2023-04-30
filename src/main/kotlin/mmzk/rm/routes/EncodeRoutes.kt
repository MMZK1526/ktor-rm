package mmzk.rm.routes

import mmzk.rm.models.EncodeRequest
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
        put {
            if (MMZKRM.path == null) {
                return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    EncodeResponse(hasError = true, errors = listOf("Unsupported Server OS!"))
                )
            }
            try {
                val rm = try {
                    call.receive<EncodeRequest>()
                } catch (e: Exception) {
                    return@put call.respond(
                        status = HttpStatusCode.BadRequest,
                        EncodeResponse(hasError = true, errors = listOf("$e"))
                    )
                }
                val output = if (rm.code == null) {
                    try {
                        MMZKRM.run(listOf("-j", "-e").plus(rm.args))
                    } catch (e: Exception) {
                        return@put call.respond(
                            status = HttpStatusCode.InternalServerError,
                            EncodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                        )
                    } ?: return@put call.respond(
                        status = HttpStatusCode.RequestTimeout,
                        EncodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                    )
                } else {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val output = try {
                        MMZKRM.run(listOf("-j", "-e", file.pathString))
                    } catch (e: Exception) {
                        return@put call.respond(
                            status = HttpStatusCode.InternalServerError,
                            EncodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                        )
                    } ?: return@put call.respond(
                        status = HttpStatusCode.RequestTimeout,
                        EncodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                    )
                    file.deleteIfExists()
                    output
                }
                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            EncodeResponse.serializer(),
                            output
                        ).hasError
                    ) HttpStatusCode.BadRequest else HttpStatusCode.OK
                )
            } catch (e: Exception) {
                return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    EncodeResponse(hasError = true, errors = listOf("$e"))
                )
            }
        }
        post {
            if (MMZKRM.path == null) {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    EncodeResponse(hasError = true, errors = listOf("Unsupported Server OS!"))
                )
            }
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
                    try {
                        MMZKRM.run(listOf("-j", "-e").plus(rm.args))
                    } catch (e: Exception) {
                        return@post call.respond(
                            status = HttpStatusCode.InternalServerError,
                            EncodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                        )
                    } ?: return@post call.respond(
                        status = HttpStatusCode.RequestTimeout,
                        EncodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                    )
                } else {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val output = try {
                        MMZKRM.run(listOf("-j", "-e", file.pathString))
                    } catch (e: Exception) {
                        return@post call.respond(
                            status = HttpStatusCode.InternalServerError,
                            EncodeResponse(hasError = true, errors = listOf("Internal Error: $e"))
                        )
                    } ?: return@post call.respond(
                        status = HttpStatusCode.RequestTimeout,
                        EncodeResponse(hasError = true, errors = listOf("The request takes too long!"))
                    )
                    file.deleteIfExists()
                    output
                }
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
