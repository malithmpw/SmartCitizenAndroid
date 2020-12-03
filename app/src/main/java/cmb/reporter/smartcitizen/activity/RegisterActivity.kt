package cmb.reporter.smartcitizen.activity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cmb.reporter.smartcitizen.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener


class RegisterActivity : BaseActivity() {
    private val RESOLVE_HINT = 1332
    private var isPhoneNumberVerified = false
    private var etPhoneNumber: EditText? = null
    private var client: SmsRetrieverClient? = null
    private var apiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        initClients()
        requestHint()
        setTitle(R.string.register_activity_name)
        etPhoneNumber = findViewById(R.id.editText_userPhoneNumber)
        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            if (isPhoneNumberVerified) {
                finish()
                startActivity(Intent(this, LandingActivity::class.java))
            }
        }
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
                if (!verifiedNumber.isNullOrEmpty()){
                    etPhoneNumber?.setText(verifiedNumber)
                    etPhoneNumber?.isEnabled = false
                }else{

                }
                /*etPhoneNumber?.text?.toString()?.let {
                    val enteredPhoneNumber = it
                    if ("+94${
                            enteredPhoneNumber.substring(
                                1,
                                enteredPhoneNumber.length
                            )
                        }" == verifiedNumber
                    ) {
                        isPhoneNumberVerified = true
                        etPhoneNumber?.setText(verifiedNumber)
                    }
                }*/
            }
        }
    }
}