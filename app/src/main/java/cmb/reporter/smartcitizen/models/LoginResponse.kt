package cmb.reporter.smartcitizen.models

data class LoginResponse(
    var id: Int,
    var firstName: String,
    var lastName: String,
    var phoneNo: String,
    var role: Role
)
