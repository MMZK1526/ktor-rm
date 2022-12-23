package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class DecodeResponse(
    val hasError: Boolean,
    val errors: List<String>? = null,
    val decodeToPair: List<String>? = null,
    val decodeToList: List<String>? = null,
    val decodeToRM: String? = null,
)
