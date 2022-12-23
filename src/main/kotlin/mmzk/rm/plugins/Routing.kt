package mmzk.rm.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mmzk.rm.routes.decodeRouting
import mmzk.rm.routes.encodeRouting

fun Application.configureRouting() {
    routing {
        get {
            call.respondText("Welcome to the Register Machine Simulator! Made by MMZK1526")
        }
        encodeRouting()
        decodeRouting()
    }
}
