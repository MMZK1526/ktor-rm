package mmzk.rm.routes

import mmzk.rm.models.RegisterMachine
import com.lordcodes.turtle.ShellLocation
import com.lordcodes.turtle.shellRun
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mmzk.rm.utilities.OS
import mmzk.rm.utilities.getOS

private val mmzkrm = when (getOS()) {
    OS.WINDOWS -> null
    OS.LINUX -> null
    OS.MAC -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/mac/")
    OS.SOLARIS -> null
    OS.OTHER -> null
}

fun Route.encodeRouting() {
    route("/encode") {
        post {
            try {
                val rm = call.receive<RegisterMachine>()
                val output =
                    mmzkrm?.let { shellRun("./mmzkrm", listOf("-e").plus(rm.args.map { it.toString() }), mmzkrm) }
                        ?: return@post call.respondText(
                            "Unsupported server OS!",
                            status = HttpStatusCode.InternalServerError
                        )

                call.respondText(output, status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("${e.javaClass.kotlin}: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
