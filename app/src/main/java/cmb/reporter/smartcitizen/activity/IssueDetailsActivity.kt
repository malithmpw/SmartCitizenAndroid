package cmb.reporter.smartcitizen.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.adapter.setImageViaGlide
import cmb.reporter.smartcitizen.models.IssueResponse


class IssueDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.issue_details_layout)
        if (intent.extras != null) {
            val issue: IssueResponse = intent.getSerializableExtra("issue") as IssueResponse
            val imageViewIssue = findViewById<ImageView>(R.id.imageView_issue_image)
            val imageViewOpenMaps = findViewById<ImageView>(R.id.imageView_open_in_map)
            val issueId = findViewById<TextView>(R.id.textView_issue_id_value)
            val issueStatus = findViewById<TextView>(R.id.textView_issue_status_value)
            val issueArea = findViewById<TextView>(R.id.textView_area_value)
            val issueAddressTo = findViewById<TextView>(R.id.textView_address_to_value)
            val issueDesc = findViewById<TextView>(R.id.textView_issue_desc_value)

            imageViewIssue.setImageViaGlide(this, issue.imageUrl)
            imageViewOpenMaps.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/@${issue.latitude},${issue.longitude},10z"))
                startActivity(intent)
            }
            issueId.text = issue.issueId
            issueStatus.text = issue.status
            issueArea.text = if (issue.area == "Any") "Not Assigned" else issue.area
            issueAddressTo.text = if (issue.category == "Any") "Not Assigned" else issue.category
            issueDesc.text = issue.description
        }
    }
}