package cmb.reporter.app.smartcitizenapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.models.LoginRequest
import cmb.reporter.app.smartcitizenapp.models.LoginResponse
import cmb.reporter.app.smartcitizenapp.security.EncryptUtil
import cmb.reporter.app.smartcitizenapp.sharedPref.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {
    private lateinit var userIdEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var progressbar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        val savedUserPhoneNumber = sharePrefUtil.getStringValue(PHONE_NUMBER)

        userIdEt = findViewById(R.id.editText_userPhoneNumber)
        savedUserPhoneNumber?.let {
            if (it.isNotEmpty()){
                userIdEt.setText(it)
            }
        }
        passwordEt = findViewById(R.id.editText_password)
        progressbar = findViewById(R.id.progressBar)

        val buttonLogin = findViewById<Button>(R.id.button_login)
        buttonLogin.setOnClickListener {
            val userId = userIdEt.text.toString()
            val password = passwordEt.text.toString()

            if (userId.isNotEmpty() && password.isNotEmpty()) {
                progressbar.visibility = View.VISIBLE
                loginUser(this, userId, EncryptUtil.encryptPassword(password))
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.user_name_or_password_incorrect),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            startActivity(Intent(this, VerifyNumberActivity::class.java))
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

        val forgotPasswordTv = findViewById<TextView>(R.id.forgot_password_textView)
        forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser(context: Context, userId: String, password: String) {
        val call = apiService.login(LoginRequest(userId, password))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        sharePrefUtil.putStringValue(USER_PASSWORD, password)
                        sharePrefUtil.putStringValue(PHONE_NUMBER, user.phoneNo)
                        sharePrefUtil.saveUser(user)
                        finish()
                        startActivity(Intent(context, LandingActivity::class.java))
                    }

                } else {
                    userIdEt.setTextColor(resources.getColor(R.color.red))
                    passwordEt.setTextColor(resources.getColor(R.color.red))
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.phone_number_or_password_incorrect),
                        Toast.LENGTH_LONG
                    ).show()
                }
                progressbar.visibility = View.GONE
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressbar.visibility = View.GONE
            }
        })
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