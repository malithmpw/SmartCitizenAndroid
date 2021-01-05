package cmb.reporter.app.smartcitizenapp.models

data class IssueUpdate(
    val id: Long,
    val status: String,
    val assignBy: User?,
    val assignee: User?
)

data class IssueResolve(
    val id: Long,
    val status: String
)