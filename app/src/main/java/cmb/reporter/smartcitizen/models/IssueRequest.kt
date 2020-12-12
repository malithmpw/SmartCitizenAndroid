package cmb.reporter.smartcitizen.models

data class IssueRequest(
    val userId: Int,
    val pageNo: Int,
    val startDate: String,
    val endDate: String,
    val status: String? = null,
    val areaId: Int? = null,
    val categoryId: Int? = null
)
