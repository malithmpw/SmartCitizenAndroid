package cmb.reporter.app.smartcitizenapp.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.models.ForgotPassword
import cmb.reporter.app.smartcitizenapp.models.RegisterUser
import cmb.reporter.app.smartcitizenapp.security.EncryptUtil
import cmb.reporter.app.smartcitizenapp.sharedPref.LAST_PHONE_NUMBER_VERIFIED_TIME
import cmb.reporter.app.smartcitizenapp.sharedPref.USER_PASSWORD
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var mVerificationId: String? = null
    private lateinit var progressbar: ProgressBar
    private lateinit var fpVerifyButton: Button
    private lateinit var fpVerifyNumberEt: EditText
    private lateinit var fpOtpEt: EditText
    private lateinit var fpNewPassLayout: LinearLayout
    private var otpSent = false
    private var phoneNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)
        mAuth = FirebaseAuth.getInstance()

        progressbar = findViewById(R.id.progressBar)
        fpVerifyNumberEt = findViewById(R.id.forgot_password_phoneNumber_et)
        fpOtpEt = findViewById(R.id.forgot_password__otp_et)
        fpVerifyButton = findViewById(R.id.forgot_password_verify_number_btn)
        fpNewPassLayout = findViewById(R.id.save_new_pass_layout)
        val fpNewPasswordEt = findViewById<EditText>(R.id.forgot_password_new_pass_et)
        val fpNewConfPassEt = findViewById<EditText>(R.id.forgot_password_conf_new_pass_et)
        val fpSaveButton = findViewById<Button>(R.id.forgot_password_save_new_btn)

        if (sharePrefUtil.is24hoursElapsed()) {
            fpVerifyButton.visibility = View.INVISIBLE
            Toast.makeText(
                this@ForgotPasswordActivity,
                getString(R.string.you_are_allowed_to_change_password_once_a_day),
                Toast.LENGTH_LONG
            ).show()
        } else {
            fpVerifyButton.visibility = View.VISIBLE
        }

        fpVerifyNumberEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.length == 10) {
                        fpVerifyButton.visibility = View.VISIBLE
                    } else {
                        fpVerifyButton.visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        fpVerifyButton.setOnClickListener {

            if (!otpSent && fpVerifyNumberEt.text.toString().isNotEmpty()) {
                otpSent = true
                fpVerifyButton.text = resources.getText(R.string.verify)
                fpOtpEt.visibility = View.VISIBLE
                progressbar.visibility = View.VISIBLE
                sharePrefUtil.putLongValue(
                    LAST_PHONE_NUMBER_VERIFIED_TIME,
                    System.currentTimeMillis()
                )

                phoneNumber = fpVerifyNumberEt.text.toString()
                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber("+94${phoneNumber!!.substring(1)}")       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                val otpText = fpOtpEt.text.toString()
                if (otpText.isNotEmpty()) {
                    verifyVerificationCode(otpText)
                } else {
                    return@setOnClickListener
                }
            }
        }

        fpSaveButton.setOnClickListener {
            val newPass = fpNewPasswordEt.text.toString()
            val newConfPass = fpNewConfPassEt.text.toString()
            progressbar.visibility = View.VISIBLE
            if (newPass.isNotEmpty() && newConfPass.isNotEmpty() && newPass == newConfPass) {
                val encryptedPass = EncryptUtil.encryptPassword(newPass)
                val call = apiService.forgotPassword(ForgotPassword(phoneNumber!!, encryptedPass))
                call.enqueue(object : Callback<RegisterUser> {
                    override fun onResponse(
                        call: Call<RegisterUser>,
                        response: Response<RegisterUser>
                    ) {
                        progressbar.visibility = View.GONE
                        if (response.isSuccessful) {
                            sharePrefUtil.putStringValue(USER_PASSWORD, encryptedPass)
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                getString(R.string.new_password_added_successfully),
                                Toast.LENGTH_LONG
                            ).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                getString(R.string.internal_error_occurred_try_again_later),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterUser>, t: Throwable) {
                        progressbar.visibility = View.GONE
                    }
                })
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    getString(R.string.passwords_are_empty_or_not_matched),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                progressbar.visibility = View.GONE
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressbar.visibility = View.GONE
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    getString(R.string.phone_number_verification_failed_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                mVerificationId = s
                progressbar.visibility = View.GONE
                //mResendToken = forceResendingToken
            }
        }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    fpVerifyButton.visibility = View.GONE
                    fpVerifyNumberEt.visibility = View.GONE
                    fpOtpEt.visibility = View.GONE
                    fpNewPassLayout.visibility = View.VISIBLE
                    //   finish()
                    // startActivity(Intent(this, LoginActivity::class.java))
                }
            }
    }
}