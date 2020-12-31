package cmb.reporter.app.smartcitizenapp.models

data class Filter(
    val startDate: String,
    val endDate: String,
    var area: Area? = null,
    var department: Category? = null,
    var status: String? = null
)