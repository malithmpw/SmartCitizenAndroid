package cmb.reporter.app.smartcitizenapp.models

data class ChangePassword(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)
