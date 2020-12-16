package cmb.reporter.smartcitizen.activity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.models.RegisterUser
import cmb.reporter.smartcitizen.models.Role
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : BaseActivity() {
    private val RESOLVE_HINT = 1332
    private var isPhoneNumberVerified = false
    private var etPhoneNumber: EditText? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var etPassword: EditText? = null
    private var confirmPassword: EditText? = null
    private var client: SmsRetrieverClient? = null
    private var apiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        initClients()
        requestHint()
        setTitle(R.string.register_activity_name)

        etPhoneNumber = findViewById(R.id.editText_userPhoneNumber)
        firstName = findViewById(R.id.editText_firstName)
        lastName = findViewById(R.id.editText_lastName)
        etPassword = findViewById(R.id.editText_password)
        confirmPassword = findViewById(R.id.editText_password_confirm)

        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            val fName = firstName?.text.toString()
            val lName = lastName?.text.toString()
            val phoneNo = etPhoneNumber?.text.toString()
            val password = etPassword?.text.toString()
            val conPassword = confirmPassword?.text.toString()
            isPhoneNumberVerified = true
            if (isPhoneNumberVerified && password == conPassword) {
                val registerUser = RegisterUser(
                    firstName = fName,
                    lastName = lName,
                    phoneNo = phoneNo,
                    password = password,
                    role = Role(id = 2, name = "USER"),
                    category = null,
                    email = null,
                    id = null
                )
                registerUser(registerUser)
            }
        }
    }

    private fun registerUser(registerUser: RegisterUser) {
        val call = apiService.register(register = registerUser)
        call.enqueue(object : Callback<RegisterUser> {
            override fun onResponse(call: Call<RegisterUser>, response: Response<RegisterUser>) {
                if (response.isSuccessful) {
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
                Toast.makeText(
                    this@RegisterActivity,
                    "User Registration failed, Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }


    private fun initClients() {
        client = SmsRetriever.getClient(this)
        apiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(connectionCallbacks)
            .enableAutoManage(this, 0, connectionFailureListener)
            .addApi(Auth.CREDENTIALS_API)
            .build()
    }

    private val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(p0: Bundle?) {
            client?.startSmsRetriever()
        }

        override fun onConnectionSuspended(p0: Int) {}
    }
    private val connectionFailureListener = OnConnectionFailedListener {
        val xxx = it
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent: PendingIntent = Auth.CredentialsApi.getHintPickerIntent(
            apiClient, hintRequest
        )
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                val verifiedNumber = credential?.id
                if (!verifiedNumber.isNullOrEmpty()) {
                    etPhoneNumber?.setText(verifiedNumber.replace("+94","0"))
                    etPhoneNumber?.isEnabled = false
                    isPhoneNumberVerified = true
                } else {

                }
            }
        }
    }
}