package cmb.reporter.app.smartcitizen.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import cmb.reporter.app.smartcitizen.AppData
import cmb.reporter.app.smartcitizen.R
import cmb.reporter.app.smartcitizen.adapter.setImageViaGlide
import cmb.reporter.app.smartcitizen.models.IssueResponse


class IssueDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.issue_details_layout)
        val issue: IssueResponse? = AppData.getSelectedIssue()
        if (issue != null) {
            val imageViewIssue = findViewById<ImageView>(R.id.imageView_issue_image)
            val imageViewOpenMaps = findViewById<ImageView>(R.id.imageView_open_in_map)
            val issueId = findViewById<TextView>(R.id.textView_issue_id_value)
            val issueStatus = findViewById<TextView>(R.id.textView_issue_status_value)
            val issueArea = findViewById<TextView>(R.id.textView_area_value)
            val issueAddressTo = findViewById<TextView>(R.id.textView_address_to_value)
            val issueDesc = findViewById<TextView>(R.id.textView_issue_desc_value)

            if (issue.imageUrl.isNotEmpty()) {
                imageViewIssue.setImageViaGlide(this, "http://95.111.198.176:9001${issue.imageUrl[0]}")
            }
            imageViewOpenMaps.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/@${issue.lat},${issue.lon},10z")
                )
                startActivity(intent)
            }
            issueId.text = issue.id.toString()
            issueStatus.text = issue.status
            issueArea.text = if (issue.area?.name == "Any") "Not Assigned" else issue.area?.name
            issueAddressTo.text = if (issue.category?.name == "Any") "Not Assigned" else issue.category?.name
            issueDesc.text = issue.description
        }
    }
}