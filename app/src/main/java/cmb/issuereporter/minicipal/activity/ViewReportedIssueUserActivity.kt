package cmb.issuereporter.minicipal.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmb.issuereporter.minicipal.R
import cmb.issuereporter.minicipal.adapter.UserIssueAdapter
import cmb.issuereporter.minicipal.models.IssueResponse

class ViewReportedIssueUserActivity : BaseActivity() {
    var adapter : UserIssueAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_reported_issue_user_layout)
        val rv = findViewById<RecyclerView>(R.id.recycleView)
        adapter = UserIssueAdapter(this, getIssues())
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)
        rv.adapter = adapter
    }


    private fun getIssues(): List<IssueResponse> {
        val list = mutableListOf<IssueResponse>()
        for (i in 1..20) {
            val issue = IssueResponse(
                issueId = "123$i",
                userId = "0714859905",
                category = getCategory(i),
                description = "Bulb is not working",
                imageUrl = getImageUrl(i),
                area = getArea(i),
                status = "OPEN",
                createdDate = "2020-12-$i",
                updatedDate = "2020-12-23",
                latitude = 6.992776,
                longitude = 80.171158, assigneeId = null, assigneeName = null
            )
            list.add(issue)
        }

        return list
    }

    fun getImageUrl(i: Int): String {
        return when (i % 3) {
            0 -> "http://image.shutterstock.com/image-photo/electric-street-light-pole-on-260nw-676061803.jpg"
            1 -> "http://image.shutterstock.com/image-photo/reinforced-concrete-light-poles-260nw-437685580.jpg"
            2 -> "http://image.shutterstock.com/image-photo/light-pole-footpath-university-campus-260nw-469891403.jpg"
            else -> "http://image.shutterstock.com/image-photo/electricity-street-pole-led-light-260nw-1434291467.jpg"
        }
    }

    fun getCategory(i: Int): String {
        return when (i % 4) {
            0 -> "RAD"
            1 -> "Water"
            2 -> "Electricity"
            else -> "Any"
        }
    }

    fun getArea(i: Int): String {
        return when (i % 8) {
            0 -> "Colombo 1"
            1 -> "Colombo 4"
            2 -> "Colombo 5"
            3 -> "Colombo 8"
            4 -> "Colombo 7"
            5 -> "Colombo 15"
            6 -> "Colombo 10"
            else -> "Any"
        }
    }
}