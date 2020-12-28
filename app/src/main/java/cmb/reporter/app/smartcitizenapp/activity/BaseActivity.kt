package cmb.reporter.app.smartcitizenapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.app.smartcitizenapp.retrofit.ServiceBuilder
import cmb.reporter.app.smartcitizenapp.retrofit.SmartCityEndpoints
import cmb.reporter.app.smartcitizenapp.sharedPref.LANGUAGE
import cmb.reporter.app.smartcitizenapp.sharedPref.SharePrefUtil

abstract class BaseActivity : AppCompatActivity() {
    val apiService = ServiceBuilder.buildService(SmartCityEndpoints::class.java)
    lateinit var sharePrefUtil: SharePrefUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharePrefUtil = SharePrefUtil(this)
        val selectedLanguage = sharePrefUtil.getStringValue(LANGUAGE)
        selectedLanguage?.let {
            sharePrefUtil.setApplicationLocale(this, selectedLanguage)
        }
    }
}