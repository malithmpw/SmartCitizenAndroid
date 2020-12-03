package cmb.reporter.smartcitizen.models

data class User(
    val firstName: String,
    val lastName: String,
    val userId: String,
    val password: String?,
    val role: String
)