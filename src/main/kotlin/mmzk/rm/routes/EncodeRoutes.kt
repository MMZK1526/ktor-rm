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
                val rm = call.receive<EncodeRequest>()
                val output = if (rm.code == null) {
                    MMZKRM.path?.let { shellRun("./mmzkrm", listOf("-j", "-e").plus(rm.args.map { arg -> arg.toString() }), it) }
                } else {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val output = MMZKRM.path?.let { shellRun("./mmzkrm", listOf("-j", "-e", file.pathString), it) }
                    file.deleteIfExists()
                    output
                } ?: return@post call.respondText(
                    "Unsupported server OS!", status = HttpStatusCode.InternalServerError
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
                call.respondText("${e.javaClass.kotlin}: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
