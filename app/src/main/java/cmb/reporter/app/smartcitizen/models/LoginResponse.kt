package cmb.reporter.app.smartcitizen.models

data class LoginResponse(
    var id: Int,
    var firstName: String,
    var lastName: String,
    var phoneNo: String,
    var role: Role
)
