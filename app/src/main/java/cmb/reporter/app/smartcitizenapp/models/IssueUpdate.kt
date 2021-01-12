package cmb.reporter.app.smartcitizenapp.models

data class IssueUpdate(
    val id: Long,
    val status: String,
    val assignBy: User?,
    val assignee: User?,
    val resolution: String?
)