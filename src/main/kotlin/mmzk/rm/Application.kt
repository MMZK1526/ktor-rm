package mmzk.rm

import io.ktor.server.application.*
import mmzk.rm.plugins.configureHTTP
import mmzk.rm.plugins.configureRouting
import mmzk.rm.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureRouting()
    configureSerialization()
    configureHTTP()
}
