package cmb.reporter.app.smartcitizen.models

data class Issue(
    val user: User,
    val category: Category?,
    val description: String?,
    val area: Area?,
    val imageToSave: List<String>,
    val status: String,
    val lat: Double,
    val lon: Double,
)

enum class IssueStatus {
    OPEN, CLOSED, ASSIGNED, TOP, NONE
}