package cmb.reporter.smartcitizen.models

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNo: String,
    val password: String?,
    val role: Role
)