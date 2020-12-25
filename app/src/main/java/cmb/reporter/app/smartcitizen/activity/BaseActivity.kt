package cmb.reporter.app.smartcitizen.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.app.smartcitizen.retrofit.ServiceBuilder
import cmb.reporter.app.smartcitizen.retrofit.SmartCityEndpoints
import cmb.reporter.app.smartcitizen.sharedPref.LANGUAGE
import cmb.reporter.app.smartcitizen.sharedPref.SharePrefUtil

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