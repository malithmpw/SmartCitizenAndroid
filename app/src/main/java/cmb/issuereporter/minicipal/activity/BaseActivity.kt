package cmb.issuereporter.minicipal.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cmb.issuereporter.minicipal.sharedPref.LANGUAGE
import cmb.issuereporter.minicipal.sharedPref.SharePrefUtil

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