package cmb.reporter.smartcitizen.models

data class AllIssueResponse(
    val pageCount: Int,
    val totalCount: Int,
    val issueList: List<IssueResponse>
)
