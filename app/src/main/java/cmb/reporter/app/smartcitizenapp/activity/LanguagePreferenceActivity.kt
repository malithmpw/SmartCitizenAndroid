package cmb.reporter.app.smartcitizenapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.sharedPref.*


class LanguagePreferenceActivity : AppCompatActivity() {
    lateinit var sharePrefUtil: SharePrefUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharePrefUtil = SharePrefUtil(this)
        val selectedLanguage = sharePrefUtil.getStringValue(LANGUAGE)
        selectedLanguage?.let {
            sharePrefUtil.setApplicationLocale(this, it)
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val buttonSinhala = findViewById<Button>(R.id.button_select_sinhala)
        buttonSinhala.setOnClickListener {
            startLogin(SI)
        }
        val buttonTamil = findViewById<Button>(R.id.button_select_tamil)
        buttonTamil.setOnClickListener {
            startLogin(TA)
        }
        val buttonEnglish = findViewById<Button>(R.id.button_select_english)
        buttonEnglish.setOnClickListener {
            startLogin(EN)
        }
    }

    private fun startLogin(language: String) {
        sharePrefUtil.putStringValue(LANGUAGE, language)
        sharePrefUtil.setApplicationLocale(this, language)
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

}