package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class SimulateResponse(
    val hasError: Boolean,
    val errors: List<String>? = null,
    val steps: Int,
    val registerValues: List<String> = listOf()
)