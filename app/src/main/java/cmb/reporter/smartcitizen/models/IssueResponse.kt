package cmb.reporter.smartcitizen.models

import java.io.Serializable

data class IssueResponse(
    val issueId: String,
    val userId: String,
    val category: String = "Any",
    val description: String?,
    val imageUrl: String,
    val area: String = "Any",
    val status: String,
    val assigneeId: String?,
    val assigneeName: String?,
    val createdDate: String,
    val updatedDate: String,
    val latitude: Double,
    val longitude: Double
) : Serializable
