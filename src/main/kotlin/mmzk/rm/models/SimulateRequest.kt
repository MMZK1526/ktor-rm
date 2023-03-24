package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class SimulateRequest(val code: String, val args: List<String> = listOf(), val startFromR0: Boolean = false)
