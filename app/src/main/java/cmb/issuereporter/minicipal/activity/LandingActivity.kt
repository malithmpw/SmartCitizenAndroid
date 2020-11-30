package cmb.issuereporter.minicipal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import cmb.issuereporter.minicipal.R
import cmb.issuereporter.minicipal.sharedPref.USER_ROLE

class LandingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_activity)
        val welcomeDescription = findViewById<TextView>(R.id.textView_landing_welcone_description)
        val viewIssueButton = findViewById<Button>(R.id.button_landing_view_issues_reported)
        val reportNewOrAssignedIssueButton =
            findViewById<Button>(R.id.button_landing_view_issues_assigned_to_me_or_report)

        var userRole = sharePrefUtil.getStringValue(USER_ROLE)
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
            if (userRole == "USER") {
                startActivity(Intent(this, ReportIssueActivity::class.java))
            } else if (userRole == "ADMIN") {
            }
        }
    }
}