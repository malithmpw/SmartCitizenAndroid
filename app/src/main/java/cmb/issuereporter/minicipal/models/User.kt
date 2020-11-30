package cmb.issuereporter.minicipal.models

data class User(
    val firstName: String,
    val lastName: String,
    val userId: String,
    val password: String?,
    val role: String
)