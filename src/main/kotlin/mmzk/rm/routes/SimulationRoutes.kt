package mmzk.rm.routes

import com.lordcodes.turtle.shellRun
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.EncodeResponse
import mmzk.rm.models.SimulateRequest
import mmzk.rm.models.SimulateResponse
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.io.path.writeText

fun Route.simulationRouting() {
    route("/simulate") {
        post {
            try {
                val rm = try {
                    call.receive<SimulateRequest>()
                } catch (e: Exception) {
                    return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        SimulateResponse(hasError = true, errors = listOf("$e"))
                    )
                }

                val output = run {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val args = if (rm.startFromR0)
                        listOf("-j", "-i", file.pathString).plus(rm.args)
                    else
                        listOf("-j", file.pathString).plus(rm.args)
                    val output = MMZKRM.path?.let {
                        shellRun("./mmzkrm", args, it)
                    }
                    file.deleteIfExists()
                    output
                } ?: return@post call.respond(
                    status = HttpStatusCode.BadRequest,
                    SimulateResponse(hasError = true, errors = listOf("Unsupported server OS!"))
                )

                call.respondText(
                    output,
                    status = if (Json.decodeFromString(
                            SimulateResponse.serializer(),
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
