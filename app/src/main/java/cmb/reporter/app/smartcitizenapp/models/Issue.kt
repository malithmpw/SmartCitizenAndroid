package cmb.reporter.app.smartcitizenapp.models

data class Issue(
    val user: User,
    val category: Category?,
    val description: String?,
    val area: Area?,
    val imageToSave: List<String>,
    val status: String,
    val lat: Double?,
    val lon: Double?,
    val directions:String? = null
)

enum class IssueStatus {
    OPEN, ASSIGNED, RESOLVED, REJECTED
}