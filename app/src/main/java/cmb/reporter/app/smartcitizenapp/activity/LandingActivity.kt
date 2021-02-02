package cmb.reporter.app.smartcitizenapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cmb.reporter.app.smartcitizenapp.AppData
import cmb.reporter.app.smartcitizenapp.BuildConfig
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.adapter.setImageViaGlide
import cmb.reporter.app.smartcitizenapp.getLocalizedString
import cmb.reporter.app.smartcitizenapp.models.*
import cmb.reporter.app.smartcitizenapp.sharedPref.SI
import cmb.reporter.app.smartcitizenapp.sharedPref.TA
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LandingActivity : BaseActivity() {
    private var userRole: String? = null
    private val PERMISSION_CODE = 1000;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_activity)
        val welcomeUserName = findViewById<TextView>(R.id.textView_landing_welcome_username)
        val welcomeDescription = findViewById<TextView>(R.id.textView_landing_welcone_description)
        val viewIssueButton = findViewById<Button>(R.id.button_landing_view_issues_reported)
        val reportNewOrAssignedIssueButton =
            findViewById<Button>(R.id.button_landing_view_issues_assigned_to_me_or_report)

        val logoImage = findViewById<ImageView>(R.id.imageView_logo)
        logoImage.setImageViaGlide(this, R.drawable.logo)

        val user: User = sharePrefUtil.getUser()
        userRole = user.role.name
        welcomeUserName.text = "${resources.getString(R.string.welcome_name)} ${user.firstName}"
        when (userRole) {
            "USER" -> {
                welcomeDescription.text = resources.getString(R.string.landing_page_message_user)
                viewIssueButton.text = resources.getString(R.string.my_reported_issues)
                reportNewOrAssignedIssueButton.text = resources.getString(R.string.report_new_issue)
            }
            "SUPERUSER" -> {
                welcomeDescription.text = resources.getString(R.string.landing_page_message_user)
                viewIssueButton.text = resources.getString(R.string.all_reported_issues)
                reportNewOrAssignedIssueButton.text = resources.getString(R.string.report_new_issue)
            }
            "ADMIN" -> {
                welcomeDescription.text = resources.getString(R.string.landing_page_message_admin)
                viewIssueButton.text = resources.getString(R.string.view_issues_reported)
                reportNewOrAssignedIssueButton.text =
                    resources.getString(R.string.issues_assigned_to_me)
            }
            "SUPERADMIN" -> {
                welcomeDescription.text = resources.getString(R.string.landing_page_message_admin)
                viewIssueButton.text = resources.getString(R.string.view_issues_reported)
                reportNewOrAssignedIssueButton.visibility = View.GONE
            }
        }

        //TODO decide which user role is logged in and change button label and functionality based on that

        viewIssueButton.setOnClickListener {
            if (userRole == "USER" || userRole == "SUPERUSER") {
                startActivity(Intent(this, ViewReportedIssueUserActivity::class.java))
            } else if (userRole == "ADMIN" || userRole == "SUPERADMIN") {
                startActivity(Intent(this, ViewReportedIssueAdminActivity::class.java))
            }
        }
        reportNewOrAssignedIssueButton.setOnClickListener {

            if (userRole == "USER" || userRole == "SUPERUSER") {
                checkPermissionBeforeReportIssue()
            } else if (userRole == "ADMIN" || userRole == "SUPERADMIN") {
                val intent = Intent(this, ViewReportedIssueAdminActivity::class.java)
                intent.putExtra("pageType", "resolveIssue")
                startActivity(intent)
            }
        }
    }

    private fun goToReportIssueActivity() {
        startActivity(Intent(this, ReportIssueActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        initAppData()
    }


    private fun checkPermissionBeforeReportIssue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                    Toast.makeText(this, resources.getString(R.string.permissions_not_granted), Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_account -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
            else -> {
            }
        }
        return true
    }

    private fun initAppData() {
        val appDataRequest = apiService.getAppData(sharePrefUtil.getUser().id)
        appDataRequest.enqueue(object : Callback<AllAppData>{
            override fun onResponse(call: Call<AllAppData>, response: Response<AllAppData>) {
                if (response.isSuccessful) {
                    val allAppData = response.body()
                    allAppData?.categoryList?.let {
                        val categories = it.toMutableList()
                        categories.add(0, Category(-1,  getLocalizedString(sharePrefUtil, AppData.selectDepartment), ""))
                        AppData.setCategories(categories, sharePrefUtil)
                    }
                    allAppData?.areas?.let {
                        val areas = it.toMutableList()
                        areas.add(0, Area(-1, getLocalizedString(sharePrefUtil, AppData.selectArea)))
                        AppData.setAreas(areas, sharePrefUtil)
                    }
                    allAppData?.adminUserList?.let {
                        val admins = it.toMutableList()
                        admins.add(0, User(id = -1, firstName =  getLocalizedString(sharePrefUtil, AppData.selectAdmin), lastName = "", phoneNo = "", password = null, role = Role(-1, "")))
                        AppData.setAdmins(admins, sharePrefUtil)
                    }
                }
            }

            override fun onFailure(call: Call<AllAppData>, t: Throwable) {

            }
        })
    }
}