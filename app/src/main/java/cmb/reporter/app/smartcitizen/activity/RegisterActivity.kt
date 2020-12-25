package cmb.reporter.app.smartcitizen.activity

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cmb.reporter.app.smartcitizen.AppData
import cmb.reporter.app.smartcitizen.BuildConfig
import cmb.reporter.app.smartcitizen.R
import cmb.reporter.app.smartcitizen.models.RegisterUser
import cmb.reporter.app.smartcitizen.models.Role
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
import kotlin.random.Random

class RegisterActivity : BaseActivity() {

    companion object {
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 35
    }

    private lateinit var progressbar: ProgressBar
    private val PERMISSION_CODE = 1100
    private val RESOLVE_HINT = 1332
    private var isPhoneNumberVerified = false
    private var isOptSent = false
    private var etPhoneNumber: EditText? = null
    private var etOTP: EditText? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var etPassword: EditText? = null
    private var confirmPassword: EditText? = null
    private var client: SmsRetrieverClient? = null
    private var apiClient: GoogleApiClient? = null

    private val generatedOtp = Random.nextInt(1000, 9999).toString()

    private val intentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val message = intent.extras!!.getString("sms")
            val splits = message?.split(":")?.toTypedArray()
            splits?.let {
                if (splits.size == 2) {
                    val code = splits[1]
                    if (generatedOtp == code) {
                        etOTP?.setText(code)
                        isPhoneNumberVerified = true
                    }
                }
            }
        }
    }

    var intentFilter: IntentFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        intentFilter = IntentFilter()
        intentFilter!!.addAction("SMS_RECEIVED_ACTION")
        initClients()
        requestSmsPermission()
        setTitle(R.string.register_activity_name)

        etPhoneNumber = findViewById(R.id.editText_userPhoneNumber)
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
            if (isPhoneNumberVerified && password == conPassword && fName.isNotEmpty()
                && lName.isNotEmpty() && password.isNotEmpty() && conPassword.isNotEmpty() && generatedOtp == etOTP?.text.toString()
            ) {
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
            } else if (!isPhoneNumberVerified && checkPermissions()) {
                val number = etPhoneNumber?.text.toString()
                val valid = isPhoneNumberValid(number)
                if (valid && etOTP?.text.toString().isNullOrEmpty()) {
                    sendSMS("+94${number.substring(1)}", "SmartCitizen OTP:${generatedOtp}")
                } else {
                    if (generatedOtp == etOTP?.text.toString()) {
                        isPhoneNumberVerified = true
                    }
                }
            } else if (!isPhoneNumberVerified && !checkPermissions()) {
                requestPermissions()
            } else if (generatedOtp != etOTP?.text.toString()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Invalid OTP",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Fields can't be Empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionStateSend = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )
        val permissionStateReceive = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        )
        return permissionStateSend == PackageManager.PERMISSION_GRANTED && permissionStateReceive == PackageManager.PERMISSION_GRANTED
    }

    private fun startSmsPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@RegisterActivity,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        startSmsPermissionRequest()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestHint()
            } else {

                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID, null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun requestSmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                requestHint()
            }
        } else {
            requestHint()
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


    private fun initClients() {
        client = SmsRetriever.getClient(this)
        apiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(connectionCallbacks)
            .enableAutoManage(this, 0, connectionFailureListener)
            .addApi(Auth.CREDENTIALS_API)
            .build()
    }

    override fun onResume() {
        registerReceiver(intentReceiver, intentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(intentReceiver)
        super.onPause()
    }

    private val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(p0: Bundle?) {
            client?.startSmsRetriever()
        }

        override fun onConnectionSuspended(p0: Int) {}
    }
    private val connectionFailureListener = OnConnectionFailedListener {}

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
                    etPhoneNumber?.setText(verifiedNumber.replace("+94", "0"))
                    sendSMS(verifiedNumber, "SmartCitizen OTP:${generatedOtp}")
                }
            }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(baseContext, "SMS sent", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        baseContext,
                        "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        baseContext,
                        "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        baseContext,
                        "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        baseContext,
                        "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(SENT))
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(baseContext, "SMS delivered", Toast.LENGTH_SHORT)
                        .show()
                    RESULT_CANCELED -> Toast.makeText(
                        baseContext,
                        "SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(DELIVERED))
        val sms: SmsManager = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, null, null)
        isOptSent = true
    }
}