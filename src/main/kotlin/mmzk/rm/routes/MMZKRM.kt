package mmzk.rm.routes

import com.lordcodes.turtle.ShellLocation
import mmzk.rm.utilities.OS
import mmzk.rm.utilities.getOS

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
    }
}
