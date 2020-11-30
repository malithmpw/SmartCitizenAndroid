package cmb.issuereporter.minicipal.models

data class Issue(
    val userId: String,
    val category: String = "Any",
    val description: String?,
    val area: String = "Any",
    val status: String,
    val latitude: Double,
    val longitude: Double,
)
