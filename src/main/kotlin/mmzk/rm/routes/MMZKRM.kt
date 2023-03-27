package mmzk.rm.routes

import com.lordcodes.turtle.ShellLocation
import mmzk.rm.utilities.OS
import mmzk.rm.utilities.getOS
import java.io.BufferedReader
import java.util.concurrent.TimeUnit

class MMZKRM {
    companion object {
        val path = when (getOS()) {
            OS.WINDOWS -> null
            OS.LINUX -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/linux/")
            OS.MAC -> run {
                val arch = System.getProperty("os.arch")
                when {
                    arch.contains("aarch") -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/mac/apple/")
                    arch.contains("x86") -> ShellLocation.CURRENT_WORKING.resolve("assets/mmzkrm/mac/intel/")
                    else -> null
                }
            }
            OS.SOLARIS -> null
            OS.OTHER -> null
        }

        fun run(arguments: List<String>): String? {
            return path?.let {
                val processBuilder = ProcessBuilder(listOf())
                    .directory(it)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                val process = processBuilder
                    .command(listOf("timeout", "1", "./mmzkrm") + arguments)
                    .start()
                process.waitFor(15, TimeUnit.SECONDS)
                if (process.exitValue() != 0) {
                    return null
                }
                process.inputStream.bufferedReader().use(BufferedReader::readText)
            }
        }
    }
}
