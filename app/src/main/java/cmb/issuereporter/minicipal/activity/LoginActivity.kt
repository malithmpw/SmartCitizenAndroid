package cmb.issuereporter.minicipal.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import cmb.issuereporter.minicipal.R
import cmb.issuereporter.minicipal.sharedPref.EN
import cmb.issuereporter.minicipal.sharedPref.LANGUAGE
import cmb.issuereporter.minicipal.sharedPref.SI
import cmb.issuereporter.minicipal.sharedPref.TA

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        val buttonLogin = findViewById<Button>(R.id.button_login)
        buttonLogin.setOnClickListener {
            finish()
            startActivity(Intent(this, LandingActivity::class.java))
        }
        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val languages = resources.getStringArray(R.array.languages)
        val spinner = findViewById<Spinner>(R.id.language_spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (position != 0) {
                        val lan = getLanguage(position)
                        sharePrefUtil.putStringValue(LANGUAGE, lan)
                        finish()
                        startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    private fun getLanguage(position: Int): String {
        return when (position) {
            1 -> SI
            2 -> TA
            3 -> EN
            else -> EN
        }
    }
}