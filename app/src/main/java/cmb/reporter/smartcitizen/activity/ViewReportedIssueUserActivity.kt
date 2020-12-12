package cmb.reporter.smartcitizen.activity

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.adapter.UserIssueAdapter
import cmb.reporter.smartcitizen.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewReportedIssueUserActivity : BaseActivity() {
    var adapter: UserIssueAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_reported_issue_user_layout)
        val rv = findViewById<RecyclerView>(R.id.recycleView)
        initAdapter(this, rv)
    }

    private fun initAdapter(context: Context, recyclerView: RecyclerView) {
        val startDate: String = "20201210"
        val endDate: String = "20201213"
        val userId = sharePrefUtil.getUser().id
        val call = apiService.getIssues(
            IssueRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                pageNo = 0
            )
        )
        call.enqueue(object : Callback<AllIssueResponse> {
            override fun onResponse(
                call: Call<AllIssueResponse>,
                response: Response<AllIssueResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.issueList
                    adapter = UserIssueAdapter(context,data?: listOf())
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = adapter
                }

            }

            override fun onFailure(call: Call<AllIssueResponse>, t: Throwable) {

            }
        })
    }
}