package cmb.reporter.app.smartcitizenapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import cmb.reporter.app.smartcitizenapp.AppData
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.adapter.setImageViaGlideRoundedCorners
import cmb.reporter.app.smartcitizenapp.models.*
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
            val issueResolveMessage = findViewById<EditText>(R.id.issue_resolve_message)

            val directionLabel = findViewById<TextView>(R.id.textView_direction_label)
            val directionValue = findViewById<TextView>(R.id.textView_directions_value)

            val resolutionLabel = findViewById<TextView>(R.id.textView_resolution_label)
            val resolutionValue = findViewById<TextView>(R.id.textView_resolution_value)

            if (!issue.directions.isNullOrEmpty()) {
                directionLabel.visibility = View.VISIBLE
                directionValue.visibility = View.VISIBLE
                directionValue.text = issue.directions
            }
            if (!issue.resolution.isNullOrEmpty()) {
                resolutionLabel.visibility = View.VISIBLE
                resolutionValue.visibility = View.VISIBLE
                resolutionValue.text = issue.resolution
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

            if (user.role.name == "ADMIN") {
                buttonLayout.visibility = View.VISIBLE

                if (issue.status == IssueStatus.ASSIGNED.name && user.id == issue.assignee?.id){
                    markAsResolvedButton.setOnClickListener {
                        val resolutionMessage = issueResolveMessage.text.toString()
                        val list = mutableListOf<IssueUpdate>()
                        list.add(IssueUpdate(issue.id.toLong(), IssueStatus.RESOLVED.name, null, null, resolutionMessage))
                        updateIssueDetails(list)
                        AppData.markedAsActionPerformedOnIssueDetailsPage(true)
                    }
                    markAsRejectedButton.setOnClickListener {
                        val resolutionMessage = issueResolveMessage.text.toString()
                        val list = mutableListOf<IssueUpdate>()
                        list.add(IssueUpdate(issue.id.toLong(), IssueStatus.REJECTED.name, null, null, resolutionMessage))
                        updateIssueDetails(list, true)
                        AppData.markedAsActionPerformedOnIssueDetailsPage(true)
                    }
                }else if (issue.status == IssueStatus.OPEN.name){
                    markAsResolvedButton.text = resources.getString(R.string.assign_to_me)
                    issueResolveMessage.visibility = View.INVISIBLE
                    markAsResolvedButton.setOnClickListener {
                        val list = mutableListOf<IssueUpdate>()
                        list.add(IssueUpdate(issue.id.toLong(), IssueStatus.ASSIGNED.name, null, null, null))
                        updateIssueDetails(list)
                        AppData.markedAsActionPerformedOnIssueDetailsPage(true)
                    }
                    markAsRejectedButton.visibility = View.INVISIBLE
                }
            }

        }
    }

    private fun updateIssueDetails(list: List<IssueUpdate>, isRejected: Boolean = false) {
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