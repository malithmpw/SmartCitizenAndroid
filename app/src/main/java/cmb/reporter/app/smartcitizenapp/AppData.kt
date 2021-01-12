package cmb.reporter.app.smartcitizenapp

import cmb.reporter.app.smartcitizenapp.models.Area
import cmb.reporter.app.smartcitizenapp.models.Category
import cmb.reporter.app.smartcitizenapp.models.IssueResponse


object AppData {
    private var areaList: List<Area>? = null
    private var categoryList: List<Category>? = null
    private var selectedIssue: IssueResponse? = null
    private var clickedOnRejectOrResolveButton:Boolean = false

    const val selectArea = "Select Area"
    const val selectDepartment = "Select Department"
    const val selectStatus = "Select Status"

    fun getAreas() = areaList ?: listOf()
    fun setAreas(areas: List<Area>) {
        areaList = areas
    }

    fun getCategory() = categoryList ?: listOf()
    fun setCategories(categories: List<Category>) {
        categoryList = categories
    }

    fun getSelectedIssue() = selectedIssue
    fun setSelectedIssue(issueResponse: IssueResponse) {
        selectedIssue = issueResponse
    }

    fun getStatus(isResolvedList:Boolean) = if (!isResolvedList) listOf(selectStatus, "OPEN", "ASSIGNED", "RESOLVED", "REJECTED") else  listOf("ASSIGNED", "RESOLVED")

    fun markedAsActionPerformedOnIssueDetailsPage(actionPerformed:Boolean){
        clickedOnRejectOrResolveButton = actionPerformed
    }

    fun isActionPerformedOnIssueDetailsPage() = clickedOnRejectOrResolveButton
}

fun getArea(areaName: String?): Area? {
    return if (areaName == null || areaName == AppData.selectArea) {
        null
    } else {
        AppData.getAreas().find { it.name == areaName }
    }
}

fun getCategory(categoryName: String?): Category? {
    return if (categoryName == null || categoryName == AppData.selectDepartment) {
        null
    } else {
        AppData.getCategory().find { it.name == categoryName }
    }
}

fun getStatus(status: String?): String? {
    return if (status == null || status == AppData.selectStatus) {
        null
    } else {
        AppData.getStatus(false).find { it == status }
    }
}