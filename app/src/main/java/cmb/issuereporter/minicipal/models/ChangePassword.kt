package cmb.issuereporter.minicipal.models

data class ChangePassword(
    val userId: String,
    val oldPassword: String,
    val newPassword: String,
    val role: String?
)
