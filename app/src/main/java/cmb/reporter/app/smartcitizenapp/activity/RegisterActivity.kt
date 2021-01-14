package cmb.reporter.app.smartcitizenapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.models.RegisterUser
import cmb.reporter.app.smartcitizenapp.models.Role
import cmb.reporter.app.smartcitizenapp.security.EncryptUtil
import cmb.reporter.app.smartcitizenapp.sharedPref.PHONE_NUMBER
import cmb.reporter.app.smartcitizenapp.sharedPref.USER_PASSWORD
import com.google.firebase.auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    private lateinit var progressbar: ProgressBar
    private var etPhoneNumber: EditText? = null
    private var etOTP: EditText? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var etPassword: EditText? = null
    private var confirmPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        etPhoneNumber = findViewById(R.id.editText_userPhoneNumber)
        etPhoneNumber?.setText(sharePrefUtil.getStringValue(PHONE_NUMBER))
        etPhoneNumber?.isEnabled = false

        etOTP = findViewById(R.id.editText_otp)
        firstName = findViewById(R.id.editText_firstName)
        lastName = findViewById(R.id.editText_lastName)
        etPassword = findViewById(R.id.editText_password)
        confirmPassword = findViewById(R.id.editText_password_confirm)
        progressbar = findViewById(R.id.progressBar)

        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            val fName = firstName?.text.toString()
            val lName = lastName?.text.toString()
            val phoneNo = etPhoneNumber?.text.toString()
            isPhoneNumberValid(phoneNo)
            val password = etPassword?.text.toString()
            val conPassword = confirmPassword?.text.toString()
            if (fName.isNotEmpty() && lName.isNotEmpty()
                && password.isNotEmpty() && conPassword.isNotEmpty()
                && password == conPassword
            ) {
                val registerUser = RegisterUser(
                    firstName = fName,
                    lastName = lName,
                    phoneNo = phoneNo,
                    password = EncryptUtil.encryptPassword(password),
                    role = Role(id = 2, name = "USER"),
                    category = null,
                    email = null,
                    id = null
                )
                registerUser(registerUser)
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Fields can't be Empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isPhoneNumberValid(number: String): Boolean {
        return if (number.length != 10) {
            Toast.makeText(
                this@RegisterActivity,
                "Invalid Phone Number",
                Toast.LENGTH_LONG
            ).show()
            false
        } else {
            true
        }
    }

    private fun registerUser(registerUser: RegisterUser) {
        progressbar.visibility = View.VISIBLE
        val call = apiService.register(register = registerUser)
        call.enqueue(object : Callback<RegisterUser> {
            override fun onResponse(call: Call<RegisterUser>, response: Response<RegisterUser>) {
                progressbar.visibility = View.GONE
                if (response.isSuccessful) {
                    sharePrefUtil.putStringValue(USER_PASSWORD, registerUser.password)
                    finish()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "User Registration failed, Please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterUser>, t: Throwable) {
                progressbar.visibility = View.GONE
                Toast.makeText(
                    this@RegisterActivity,
                    "User Registration failed, Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }
}