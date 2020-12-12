package cmb.reporter.smartcitizen

import cmb.reporter.smartcitizen.models.Area
import cmb.reporter.smartcitizen.models.Category
import cmb.reporter.smartcitizen.models.IssueResponse


object AppData {
    private var areaList: List<Area>? = null
    private var categoryList: List<Category>? = null
    private var selectedIssue: IssueResponse? = null

    fun getAreas() = areaList ?: listOf()
    fun setAreas(areas: List<Area>) {
        areaList = areas
    }

    fun getCategory() = categoryList ?: listOf()
    fun setCategories(categories: List<Category>) {
        categoryList = categories
    }

    fun getSelectedIssue() = selectedIssue
    fun setSelectedIssue(issueResponse: IssueResponse){
        selectedIssue = issueResponse
    }
}