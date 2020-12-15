package cmb.reporter.smartcitizen.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmb.reporter.smartcitizen.AppData
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.adapter.SmartCitizenSpinnerAdapter
import cmb.reporter.smartcitizen.adapter.UserIssueAdapter
import cmb.reporter.smartcitizen.getArea
import cmb.reporter.smartcitizen.getCategory
import cmb.reporter.smartcitizen.models.AllIssueResponse
import cmb.reporter.smartcitizen.models.Area
import cmb.reporter.smartcitizen.models.Category
import cmb.reporter.smartcitizen.models.IssueRequest
import io.blackbox_vision.datetimepickeredittext.view.DatePickerInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ViewReportedIssueUserActivity : BaseActivity(), LifecycleOwner {
    var adapter: UserIssueAdapter? = null
    var filterLayout: ConstraintLayout? = null
    var isFilterVisible = false
    var currentPageNo = 0
    private var areaSpinner: Spinner? = null
    private var categorySpinner: Spinner? = null

    private var etFromDate: DatePickerInputEditText? = null
    private var etToDate: DatePickerInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_reported_issue_user_layout)
        val rv = findViewById<RecyclerView>(R.id.recycleView)
        filterLayout = findViewById(R.id.filter_layout)
        areaSpinner = findViewById(R.id.filter_spinner_area)
        categorySpinner = findViewById(R.id.filter_spinner_department)
        etToDate = findViewById(R.id.et_to_date)
        etFromDate = findViewById(R.id.et_from_date)
        val filterButton = findViewById<Button>(R.id.filter_issue_button)
        filterButton.setOnClickListener {
            filterLayout?.visibility = View.GONE
            adapter?.clearData()
            currentPageNo = 0
            requestDataFromServer(currentPageNo)
        }

        val c1 = Calendar.getInstance()
        val sYear1 = c1.get(Calendar.YEAR)
        val sMonth1 = c1.get(Calendar.MONTH)
        val sDate1 = c1.get(Calendar.DAY_OF_MONTH)
        etToDate?.setText("${sYear1}/${sMonth1 + 1}/${sDate1}")
        c1.add(Calendar.DAY_OF_MONTH,1)
        etToDate?.setDate(c1)
        val c2 = Calendar.getInstance()
        c2.add(Calendar.MONTH, -1)
        val eYear2 = c2.get(Calendar.YEAR)
        val eMonth2 = c2.get(Calendar.MONTH)
        val eDate2 = c2.get(Calendar.DAY_OF_MONTH)
        etFromDate?.setText("${eYear2}/${eMonth2 + 1}/${eDate2}")
        c2.add(Calendar.DAY_OF_MONTH, -1)
        etFromDate?.setDate(c2)
        initSpinners()
        initAdapter(this, rv)
        requestDataFromServer(currentPageNo)
    }

    private fun initAdapter(context: Context, recyclerView: RecyclerView) {
        adapter = UserIssueAdapter(context)
        val llm = LinearLayoutManager(context)
        recyclerView.layoutManager = llm
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    requestDataFromServer(pageNo = currentPageNo)
                }
            }
        })
    }

    private fun initSpinners() {
        val areaAdapter = SmartCitizenSpinnerAdapter(this, AppData.getAreas().map { it.name })
        areaSpinner?.let {
            it.adapter = areaAdapter
        }

        val categoryAdapter =
            SmartCitizenSpinnerAdapter(this, AppData.getCategory().map { it.name })
        categorySpinner?.let {
            it.adapter = categoryAdapter
        }
    }

    private fun getIssue(
        pageNo: Int,
        userId: Int,
        startDate: String,
        endDate: String,
        area: Area?,
        category: Category?
    ): IssueRequest {
        if (area == null && category == null) {
            return IssueRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                pageNo = pageNo
            )
        } else if (area != null && category == null) {
            return IssueRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                pageNo = pageNo,
                areaId = area.id
            )
        } else if (area == null && category != null) {
            return IssueRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                pageNo = pageNo,
                categoryId = category.id
            )
        } else {
            return IssueRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                pageNo = pageNo,
                areaId = area?.id,
                categoryId = category?.id
            )
        }
    }

    private fun getDateString(calendar: Calendar): String{
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH)+1}${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun requestDataFromServer(pageNo: Int) {
        val startDate: String = getDateString(etFromDate?.getDate()!!)
        val endDate: String = getDateString(etToDate?.getDate()!!)
        val area = getArea((if (areaSpinner == null) 0 else areaSpinner!!.selectedItemId).toInt())
        val department =
            getCategory((if (categorySpinner == null) 0 else categorySpinner!!.selectedItemId).toInt())
        val userId = sharePrefUtil.getUser().id

        val issue = getIssue(
            pageNo = pageNo,
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            area = area,
            category = department
        )
        val call = apiService.getIssues(
            issueRequest = issue
        )
        currentPageNo++
        call.enqueue(object : Callback<AllIssueResponse> {
            override fun onResponse(
                call: Call<AllIssueResponse>,
                response: Response<AllIssueResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.issueList
                    adapter?.updateData(data!!)
                }

            }

            override fun onFailure(call: Call<AllIssueResponse>, t: Throwable) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                if (!isFilterVisible) {
                    isFilterVisible = true
                    filterLayout?.visibility = View.VISIBLE
                } else {
                    isFilterVisible = false
                    filterLayout?.visibility = View.GONE
                }
            }
        }
        return true
    }
}