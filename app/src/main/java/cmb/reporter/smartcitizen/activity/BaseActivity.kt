package cmb.reporter.smartcitizen.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.smartcitizen.sharedPref.LANGUAGE
import cmb.reporter.smartcitizen.sharedPref.SharePrefUtil

abstract class BaseActivity : AppCompatActivity() {
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