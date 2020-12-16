package cmb.reporter.smartcitizen.models

data class RegisterUser(
    val firstName: String,
    val lastName: String,
    val phoneNo: String,
    val password: String,
    val role: Role,
    val category: Category?,
    val email: String?,
    val id: Int?
)