package mmzk.rm.models

import kotlinx.serialization.Serializable

@Serializable
data class EncodeRequest(val code: String? = null, val args: List<Int> = listOf())
