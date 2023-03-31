package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class SimulateResponse(
    val hasError: Boolean,
    val errors: List<String>? = null,
    val steps: String? = null,
    val registerValues: List<String> = listOf(),
    val pcSnapshots: List<String>? = null,
    val registerSnapshots: List<List<String>>? = null
)
