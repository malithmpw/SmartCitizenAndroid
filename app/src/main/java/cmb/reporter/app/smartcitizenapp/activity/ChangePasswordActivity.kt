package cmb.reporter.app.smartcitizenapp.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.models.ChangePassword
import cmb.reporter.app.smartcitizenapp.models.LoginResponse
import cmb.reporter.app.smartcitizenapp.models.RegisterUser
import cmb.reporter.app.smartcitizenapp.security.EncryptUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseActivity() {
    private lateinit var progressbar: ProgressBar
    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password_layout)
        progressbar = findViewById(R.id.progressBar)
        val user = sharePrefUtil.getUser()
        val name = findViewById<TextView>(R.id.textView_account_name)
        val role = findViewById<TextView>(R.id.textView_account_role)

        etOldPassword = findViewById(R.id.editText_old_password)
        etNewPassword = findViewById(R.id.editText_new_password)
        etConfirmNewPassword = findViewById(R.id.editText_confirm_new_password)

        val updatePasswordButton = findViewById<Button>(R.id.button_account_change_password)
        name.text = "${user.firstName} ${user.lastName}"
        role.text = "${user.role.name}"


        updatePasswordButton.setOnClickListener {
            val oldpass = etOldPassword.text.toString()
            val newpass = etNewPassword.text.toString()
            val confirmNewPass = etConfirmNewPassword.text.toString()

            if (!newpass.isNullOrEmpty() && !confirmNewPass.isNullOrEmpty() && !oldpass.isNullOrEmpty()) {
                if (newpass == confirmNewPass) {
                    val encryptedOld = EncryptUtil.encryptPassword(oldpass)
                    val encryptedNew = EncryptUtil.encryptPassword(newpass)
                    changePassword(
                        sharePrefUtil.getUser().phoneNo,
                        encryptedOld,
                        encryptedNew
                    )
                } else {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "New password does not match with Confirm password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Password can't be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun changePassword(
        userPhone: String,
        oldpassword: String,
        newPassword: String
    ) {
        progressbar.visibility = View.VISIBLE
        val call = apiService.changePassword(ChangePassword(userPhone, oldpassword, newPassword))

        call.enqueue(object : Callback<RegisterUser> {
            override fun onResponse(call: Call<RegisterUser>, response: Response<RegisterUser>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        sharePrefUtil.saveUser(
                            LoginResponse(
                                user.id!!,
                                user.firstName,
                                user.lastName,
                                user.phoneNo,
                                user.role
                            )
                        )
                        etOldPassword.setText("")
                        etNewPassword.setText("")
                        etConfirmNewPassword.setText("")
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Password updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Error Occurred while updating the password, please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
                progressbar.visibility = View.GONE
            }

            override fun onFailure(call: Call<RegisterUser>, t: Throwable) {
                progressbar.visibility = View.GONE
            }

        })
    }
}