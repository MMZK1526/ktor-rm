package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class SimulateRequest(val code: String? = null, val args: List<Int> = listOf(), val startFromR0: Boolean = false)
