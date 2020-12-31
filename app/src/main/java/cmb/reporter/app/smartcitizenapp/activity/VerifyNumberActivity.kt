package cmb.reporter.app.smartcitizenapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.sharedPref.PHONE_NUMBER
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class VerifyNumberActivity : BaseActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var isVerifyButtonClicked = false
    private var mVerificationId: String? = null
    private lateinit var etPhone: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_number_layout)
        mAuth = FirebaseAuth.getInstance()

        etPhone = findViewById(R.id.editText_userPhoneNumber)
        val otp = findViewById<EditText>(R.id.editText_otp)
        val button = findViewById<Button>(R.id.button_verify_number)

        button.setOnClickListener {
            if (!isVerifyButtonClicked) {
                isVerifyButtonClicked = true
                button.text = resources.getString(R.string.verify_phone_number)
                val phoneNo = etPhone?.text.toString()
                when {
                    phoneNo.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "Phone Number can't be empty!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    phoneNo.length != 10 -> {
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "Invalid Phone Number",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        val options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+94${phoneNo.substring(1)}")       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "OTP sent to your Number",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                button.text = resources.getString(R.string.send_otp)
                val otp = otp?.text.toString()
                if (otp.isNotEmpty()) {
                    verifyVerificationCode(otp)
                } else {
                    Toast.makeText(
                        this@VerifyNumberActivity,
                        "Password is Empty",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@VerifyNumberActivity,
                    "Phone Number Verification Failed, try again later",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                mVerificationId = s
                //mResendToken = forceResendingToken
            }
        }

    private fun verifyVerificationCode(code: String) {
        sharePrefUtil.putStringValue(PHONE_NUMBER, etPhone.text.toString())
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    finish()
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
    }
}