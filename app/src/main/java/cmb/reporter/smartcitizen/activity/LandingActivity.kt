package cmb.reporter.smartcitizen.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cmb.reporter.smartcitizen.BuildConfig
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.sharedPref.USER_ROLE

class LandingActivity : BaseActivity() {
    var userRole: String? = null
    private val PERMISSION_CODE = 1000;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_activity)
        val welcomeDescription = findViewById<TextView>(R.id.textView_landing_welcone_description)
        val viewIssueButton = findViewById<Button>(R.id.button_landing_view_issues_reported)
        val reportNewOrAssignedIssueButton =
            findViewById<Button>(R.id.button_landing_view_issues_assigned_to_me_or_report)

        userRole = sharePrefUtil.getStringValue(USER_ROLE)
        userRole = "USER"
        if (userRole == "USER") {
            welcomeDescription.text = resources.getString(R.string.landing_page_message_user)
            viewIssueButton.text = resources.getString(R.string.my_reported_issues)
            reportNewOrAssignedIssueButton.text = resources.getString(R.string.report_new_issue)
        } else if (userRole == "ADMIN") {
            welcomeDescription.text = resources.getString(R.string.landing_page_message_admin)
            viewIssueButton.text = resources.getString(R.string.view_issues_reported)
            reportNewOrAssignedIssueButton.text =
                resources.getString(R.string.issues_assigned_to_me)
        }

        //TODO decide which user role is logged in and change button label and functionality based on that

        viewIssueButton.setOnClickListener {
            if (userRole == "USER") {
                startActivity(Intent(this, ViewReportedIssueUserActivity::class.java))
            } else if (userRole == "ADMIN") {
            }
        }
        reportNewOrAssignedIssueButton.setOnClickListener {
            checkPermissionBeforeReportIssue()
        }
    }

    private fun goToReportIssueActivity() {
        if (userRole == "USER") {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        } else if (userRole == "ADMIN") {
        }
    }


    private fun checkPermissionBeforeReportIssue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                goToReportIssueActivity()
            }
        } else {
            //system os is < marshmallow
            goToReportIssueActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && allPermissionGranted(grantResults)) {
                    //permission from popup was granted
                    goToReportIssueActivity()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
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
    }

    private fun allPermissionGranted(grantResults: IntArray): Boolean {
        grantResults.forEach {
            if (it == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
}