package mmzk.rm

import kotlinx.serialization.Serializable

@Serializable
data class EncodeResponse(
    val hasError: Boolean,
    val errors: List<String>? = null,
    val encodeFromList: EncodeNum? = null,
    val encodeFromPair: EncodeNum? = null,
    val encodeFromRM: EncodeNum? = null,
    val encodeToLine: List<EncodeNum>? = null
)

@Serializable
data class EncodeNum(val isTooBig: Boolean, val num: String? = null)
