package cmb.issuereporter.minicipal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import cmb.issuereporter.minicipal.R

class RegisterActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        setTitle(R.string.register_activity_name)
        val buttonEnglish = findViewById<Button>(R.id.button_register)
        buttonEnglish.setOnClickListener {
            finish()
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }
}