package cmb.reporter.app.smartcitizenapp.models

data class AllIssueResponse(
    val pageCount: Int,
    val totalCount: Int,
    val issueList: List<IssueResponse>
)
