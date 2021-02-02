package cmb.reporter.app.smartcitizenapp

import cmb.reporter.app.smartcitizenapp.models.Area
import cmb.reporter.app.smartcitizenapp.models.Category
import cmb.reporter.app.smartcitizenapp.models.IssueResponse
import cmb.reporter.app.smartcitizenapp.models.User
import cmb.reporter.app.smartcitizenapp.sharedPref.*
import com.google.gson.Gson


object AppData {
    private var areaList: List<Area> = mutableListOf()
    private var categoryList: List<Category> = mutableListOf()
    private var adminList: List<User> = mutableListOf()
    private var selectedIssue: IssueResponse? = null
    private var clickedOnRejectOrResolveButton: Boolean = false

    const val selectArea = "Select Area"
    const val selectAdmin = "Select Admin"
    const val selectDepartment = "Select Department"
    const val selectStatus = "Select Status"

    fun getAreas(sharePrefUtil: SharePrefUtil): List<Area> {
        val areaData = sharePrefUtil.getStringValue(AREA_DATA)
        val gson = Gson()
        val obj: Areas = gson.fromJson(areaData, Areas::class.java)
        if (areaList.isEmpty() && obj.areas.isEmpty()) {
            return listOf()
        } else if (areaList.isEmpty() && obj.areas.isNotEmpty()) {
            areaList = obj.areas
        }
        return areaList
    }

    fun setAreas(areas: List<Area>, sharePrefUtil: SharePrefUtil) {
        areaList = areas
        val gson = Gson()
        val json = gson.toJson(Areas(areas))
        sharePrefUtil.putStringValue(AREA_DATA, json)
    }

    fun getCategory(sharePrefUtil: SharePrefUtil): List<Category> {
        val categoryData = sharePrefUtil.getStringValue(CATEGORY_DATA)
        val gson = Gson()
        val obj: Categories = gson.fromJson(categoryData, Categories::class.java)
        if (categoryList.isEmpty() && obj.categories.isEmpty()) {
            return listOf()
        } else if (categoryList.isEmpty() && obj.categories.isNotEmpty()) {
            categoryList = obj.categories
        }
        return categoryList
    }

    fun getAdmins(sharePrefUtil: SharePrefUtil): List<User> {
        val adminData = sharePrefUtil.getStringValue(ADMIN_DATA)
        val gson = Gson()
        val obj: Admins = gson.fromJson(adminData, Admins::class.java)
        if (adminList.isEmpty() && obj.admins.isEmpty()) {
            return listOf()
        } else if (adminList.isEmpty() && obj.admins.isNotEmpty()) {
            adminList = obj.admins
        }
        return adminList
    }

    fun setAdmins(admins: List<User>, sharePrefUtil: SharePrefUtil) {
        adminList = admins
        val gson = Gson()
        val json = gson.toJson(Admins(adminList))
        sharePrefUtil.putStringValue(ADMIN_DATA, json)
    }


    fun setCategories(categories: List<Category>, sharePrefUtil: SharePrefUtil) {
        categoryList = categories
        val gson = Gson()
        val json = gson.toJson(Categories(categories))
        sharePrefUtil.putStringValue(CATEGORY_DATA, json)
    }

    fun getSelectedIssue() = selectedIssue
    fun setSelectedIssue(issueResponse: IssueResponse) {
        selectedIssue = issueResponse
    }

    fun getStatus(sharePrefUtil: SharePrefUtil, isResolvedList: Boolean) = if (!isResolvedList) listOf(
        getLocalizedString(sharePrefUtil, selectStatus),
        "OPEN",
        "ASSIGNED",
        "RESOLVED",
        "REJECTED"
    ) else listOf("ASSIGNED", "RESOLVED", "REJECTED")

    fun markedAsActionPerformedOnIssueDetailsPage(actionPerformed: Boolean) {
        clickedOnRejectOrResolveButton = actionPerformed
    }

    fun isActionPerformedOnIssueDetailsPage() = clickedOnRejectOrResolveButton
}

fun getArea(areaName: String?, sharePrefUtil: SharePrefUtil): Area? {
    return if (areaName == null || areaName == getLocalizedString(sharePrefUtil,AppData.selectArea)) {
        null
    } else {
        AppData.getAreas(sharePrefUtil).find { it.name == areaName }
    }
}

fun getCategory(categoryName: String?, sharePrefUtil: SharePrefUtil): Category? {
    return if (categoryName == null || categoryName == getLocalizedString(sharePrefUtil,AppData.selectDepartment)) {
        null
    } else {
        AppData.getCategory(sharePrefUtil).find { it.name == categoryName }
    }
}

fun getStatus(sharePrefUtil: SharePrefUtil, status: String?): String? {
    return if (status == null || status == getLocalizedString(sharePrefUtil,AppData.selectStatus)) {
        null
    } else {
        AppData.getStatus(sharePrefUtil, false).find { it == status }
    }
}

fun getLocalizedString(sharePrefUtil: SharePrefUtil, key: String): String {
    var locale:String = EN
    val user = sharePrefUtil.getUser()
    if (user.role.name == "USER"){
        locale = sharePrefUtil.getStringValue(LANGUAGE)?: EN
    }
    return filterDefaultMap[locale+key] ?:""
}

val filterDefaultMap = mapOf(
    EN + AppData.selectAdmin to AppData.selectAdmin,
    SI + AppData.selectAdmin to "පරිපාලක තෝරන්න",
    TA + AppData.selectAdmin to "தேர்ந்தெடு நிர்வாகம்",
    EN + AppData.selectArea to AppData.selectArea,
    SI + AppData.selectArea to "ප්\u200Dරදේශය තෝරන්න",
    TA + AppData.selectArea to "பகுதியைத் தேர்ந்தெடுக்கவும்",
    EN + AppData.selectDepartment to AppData.selectDepartment,
    SI + AppData.selectDepartment to "දෙපාර්තමේන්තුව තෝරන්න",
    TA + AppData.selectDepartment to "துறையைத் தேர்ந்தெடுக்கவும்",
    EN + AppData.selectStatus to AppData.selectStatus,
    SI + AppData.selectStatus to "ප්\u200Dරගතිය තෝරන්න",
    TA + AppData.selectStatus to "முன்னேற்றம்",
)

data class Areas(val areas: List<Area>)
data class Categories(val categories: List<Category>)
data class Admins(val admins: List<User>)