package cmb.reporter.app.smartcitizenapp.models

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNo: String,
    val password: String?,
    val role: Role
)

fun User.isAdmin():Boolean{
    return this.role.name == "ADMIN"
}
fun User.isUser():Boolean{
    return this.role.name == "USER"
}
fun User.isSuperUser():Boolean{
    return this.role.name == "SUPERUSER"
}
fun User.isSuperAdmin():Boolean{
    return this.role.name == "SUPERADMIN"
}