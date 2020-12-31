package cmb.reporter.app.smartcitizenapp.models

import java.io.Serializable

data class
IssueResponse(
    val id: Int,
    val description: String?,
    val imageUrl: List<String>,
    val status: String,
    val lat: Double,
    val lon: Double,
    val createdDate: String,
    val updatedDate: String,
    val category: Category?,
    val area: Area?,
    val user: User,
    val assignee: String?,
    val assignBy: String?,
    var isSelected: Boolean = false
): Serializable
