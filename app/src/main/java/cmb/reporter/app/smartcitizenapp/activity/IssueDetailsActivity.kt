package cmb.reporter.app.smartcitizenapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import cmb.reporter.app.smartcitizenapp.AppData
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.adapter.setImageViaGlideRoundedCorners
import cmb.reporter.app.smartcitizenapp.models.IssueResponse
import cmb.reporter.app.smartcitizenapp.models.IssueStatus
import cmb.reporter.app.smartcitizenapp.models.IssueUpdate
import cmb.reporter.app.smartcitizenapp.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IssueDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.issue_details_layout)
        val user: User = sharePrefUtil.getUser()
        val issue: IssueResponse? = AppData.getSelectedIssue()
        if (issue != null) {
            val imageViewIssue = findViewById<ImageView>(R.id.imageView_issue_image)
            val imageViewOpenMaps = findViewById<ImageView>(R.id.imageView_open_in_map)
            val issueId = findViewById<TextView>(R.id.textView_issue_id_value)
            val issueStatus = findViewById<TextView>(R.id.textView_issue_status_value)
            val issueArea = findViewById<TextView>(R.id.textView_area_value)
            val issueAddressTo = findViewById<TextView>(R.id.textView_address_to_value)
            val issueDesc = findViewById<TextView>(R.id.textView_issue_desc_value)
            val markAsResolvedButton = findViewById<Button>(R.id.marked_as_resolved)
            val markAsRejectedButton = findViewById<Button>(R.id.marked_as_rejected)
            val buttonLayout = findViewById<LinearLayout>(R.id.button_layout_issue_details)

            val directionLabel = findViewById<TextView>(R.id.textView_direction_label)
            val directionValue = findViewById<TextView>(R.id.textView_directions_value)
            if (!issue.directions.isNullOrEmpty()) {
                directionLabel.visibility = View.VISIBLE
                directionValue.visibility = View.VISIBLE
                directionValue.text = issue.directions
            }

            if (issue.imageUrl.isNotEmpty()) {
                imageViewIssue.setImageViaGlideRoundedCorners(
                    this,
                    "http://95.111.198.176:9001${issue.imageUrl[0]}"
                )
            }
            imageViewOpenMaps.setOnClickListener {
                if (issue.directions.isNullOrEmpty()) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/@${issue.lat},${issue.lon},10z")
                    )
                    startActivity(intent)
                }else{
                    Toast.makeText(
                        this@IssueDetailsActivity,
                        resources.getString(R.string.location_data_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            issueId.text = issue.id.toString()
            issueStatus.text = issue.status
            issueArea.text = issue.area?.name ?: resources.getString(R.string.area_unknown)
            issueAddressTo.text =
                issue.category?.name ?: resources.getString(R.string.department_unknown)
            issueDesc.text = issue.description

            if (user.role.name == "ADMIN" && issue.status == IssueStatus.ASSIGNED.name && user.id == issue.assignee?.id) {
                buttonLayout.visibility = View.VISIBLE
                markAsResolvedButton.setOnClickListener {
                    val list = mutableListOf<IssueUpdate>()
                    list.add(IssueUpdate(issue.id.toLong(), IssueStatus.RESOLVED.name, null, null))
                    markedAsResolvedOrRejected(list)
                }
                markAsRejectedButton.setOnClickListener {
                    val list = mutableListOf<IssueUpdate>()
                    list.add(IssueUpdate(issue.id.toLong(), IssueStatus.REJECTED.name, null, null))
                    markedAsResolvedOrRejected(list, true)
                }
            }

        }
    }

    private fun markedAsResolvedOrRejected(list: List<IssueUpdate>, isRejected: Boolean = false) {
        val call = apiService.updateIssues(list)
        call.enqueue(object : Callback<List<IssueResponse>> {
            override fun onResponse(
                call: Call<List<IssueResponse>>,
                response: Response<List<IssueResponse>>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@IssueDetailsActivity,
                        resources.getString(if (isRejected) R.string.mark_as_rejected_successfully else R.string.mark_as_resolved_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<List<IssueResponse>>, t: Throwable) {
                Toast.makeText(
                    this@IssueDetailsActivity,
                    resources.getString(if (isRejected) R.string.failed_to_reject_try_again else R.string.failed_to_resolve_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }
}