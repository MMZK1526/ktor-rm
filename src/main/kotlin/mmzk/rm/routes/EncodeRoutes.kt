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
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.io.path.writeText

private val mmzkrm = when (getOS()) {
    OS.WINDOWS -> null
    OS.LINUX -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/linux/")
    OS.MAC -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/mac/")
    OS.SOLARIS -> null
    OS.OTHER -> null
}

fun Route.encodeRouting() {
    route("/encode") {
        post {
            try {
                val rm = call.receive<RegisterMachine>()
                val output = if (rm.code == null) {
                    mmzkrm?.let { shellRun("./mmzkrm", listOf("-j", "-e").plus(rm.args.map { it.toString() }), mmzkrm) }
                } else {
                    val file = kotlin.io.path.createTempFile(suffix = ".mmzk")
                    file.writeText(rm.code)
                    val output = mmzkrm?.let { shellRun("./mmzkrm", listOf("-j", "-e", file.pathString), mmzkrm) }
                    file.deleteIfExists()
                    output
                } ?: return@post call.respondText(
                    "Unsupported server OS!", status = HttpStatusCode.InternalServerError
                )

                call.respondText(
                    output, status = HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText("${e.javaClass.kotlin}: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
