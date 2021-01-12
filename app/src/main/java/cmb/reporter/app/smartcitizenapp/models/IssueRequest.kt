package cmb.reporter.app.smartcitizenapp.models

data class IssueRequest(
    val userId: Int,
    val pageNo: Int,
    val startDate: String,
    val endDate: String,
    var status: String? = null,
    var areaId: Int? = null,
    var categoryId: Int? = null,
    var allIssue:Boolean = true
)
