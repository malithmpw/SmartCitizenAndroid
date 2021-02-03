package cmb.reporter.app.smartcitizenapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.app.smartcitizenapp.models.User
import cmb.reporter.app.smartcitizenapp.retrofit.ServiceBuilder
import cmb.reporter.app.smartcitizenapp.retrofit.SmartCityEndpoints
import cmb.reporter.app.smartcitizenapp.sharedPref.LANGUAGE
import cmb.reporter.app.smartcitizenapp.sharedPref.SharePrefUtil
import cmb.reporter.app.smartcitizenapp.sharedPref.USER

abstract class BaseActivity : AppCompatActivity() {
    val apiService = ServiceBuilder.buildService(SmartCityEndpoints::class.java)
    lateinit var sharePrefUtil: SharePrefUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharePrefUtil = SharePrefUtil(this)
        val selectedLanguage = sharePrefUtil.getStringValue(LANGUAGE)
        val userJson = sharePrefUtil.getStringValue(USER)
        var user : User? = null
        userJson?.let {
            user = sharePrefUtil.getUser()
        }
        if (user?.role?.name == "USER" || userJson.isNullOrEmpty()) {
            selectedLanguage?.let {
                sharePrefUtil.setApplicationLocale(this, selectedLanguage)
            }
        }
    }
}