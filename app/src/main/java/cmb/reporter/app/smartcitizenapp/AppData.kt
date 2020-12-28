package cmb.reporter.app.smartcitizenapp

import cmb.reporter.app.smartcitizenapp.models.Area
import cmb.reporter.app.smartcitizenapp.models.Category
import cmb.reporter.app.smartcitizenapp.models.IssueResponse


object AppData {
    private var areaList: List<Area>? = null
    private var categoryList: List<Category>? = null
    private var selectedIssue: IssueResponse? = null

    const val selectArea = "Select Area"
    const val selectDepartment = "Select Department"

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
        AppData.getCategory().find { it.name ==categoryName }
    }
}