package cmb.reporter.app.smartcitizen.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.app.smartcitizen.R
import cmb.reporter.app.smartcitizen.sharedPref.LANGUAGE
import cmb.reporter.app.smartcitizen.sharedPref.SharePrefUtil

class SplashActivity : AppCompatActivity() {
    lateinit var sharePrefUtil: SharePrefUtil
    private val SPLASH_TIME_OUT: Long = 2000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        sharePrefUtil = SharePrefUtil(this)
        val selectedLanguage = sharePrefUtil.getStringValue(LANGUAGE)
        Handler().postDelayed({
            if (selectedLanguage.isNullOrEmpty()) {
                startActivity(Intent(this, LanguagePreferenceActivity::class.java))
            } else {
              //  sharePrefUtil.setApplicationLocale(this, selectedLanguage)
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT)
    }
}