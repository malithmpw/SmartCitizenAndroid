package cmb.reporter.app.smartcitizenapp.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmb.reporter.app.smartcitizenapp.*
import cmb.reporter.app.smartcitizenapp.adapter.EndlessRecyclerViewScrollListener
import cmb.reporter.app.smartcitizenapp.adapter.SmartCitizenSpinnerAdapter
import cmb.reporter.app.smartcitizenapp.adapter.UserIssueAdapter
import cmb.reporter.app.smartcitizenapp.models.*
import cmb.reporter.app.smartcitizenapp.models.Filter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ViewReportedIssueAdminActivity : BaseActivity(), LifecycleOwner {
    lateinit var adapter: UserIssueAdapter
    lateinit var appliedFilter: Filter
    var isFilterVisible = false
    var currentPageNo = 0
    private lateinit var progressbar: ProgressBar
    private var pageCount: Int? = 1
    private var isSelectAllSelected = false
    private var isSelectAllButtonClicked = false
    private lateinit var selectAllButton: Button
    private var pageType: String? = null

    private lateinit var filterBottomSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_reported_issue_admin_layout)

        pageType = intent.getStringExtra("pageType")
        val rv = findViewById<RecyclerView>(R.id.recycleView)

        progressbar = findViewById(R.id.progressBar)
        filterBottomSheet = createBottomSheetDialog(this)


        selectAllButton = findViewById(R.id.admin_select_all)
        selectAllButton.setOnClickListener {
            isSelectAllButtonClicked = true
            if (!isSelectAllSelected) {
                isSelectAllSelected = true
                selectAllButton.text = resources.getText(R.string.deselect_all)
                adapter.changeSelectedState(isSelectAllSelected, !pageType.isNullOrEmpty())
            } else {
                isSelectAllSelected = false
                selectAllButton.text = resources.getText(R.string.select_all)
                adapter.changeSelectedState(isSelectAllSelected, !pageType.isNullOrEmpty())
            }
        }
        val assignToMeOrAdmin = findViewById<Button>(R.id.assign_to_admin)
        if (!pageType.isNullOrEmpty()) {
            assignToMeOrAdmin.setText(R.string.resolve)
        }
        assignToMeOrAdmin.setOnClickListener {
            val user = sharePrefUtil.getUser()
            if (user.role.name == "ADMIN") {
                val selectedIssues = adapter.getSelectedItems(!pageType.isNullOrEmpty())
                updateAssignToMe(selectedIssues, !pageType.isNullOrEmpty())
            } else if (user.role.name == "SUPERADMIN") {
//TODO super admin flow
            }
        }

        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        c2.add(Calendar.MONTH, -1)
        initAdapter(this, rv)
        appliedFilter =
            Filter(startDate = c2.getFormattedDateString(), endDate = c1.getFormattedDateString())
        if (!pageType.isNullOrEmpty()) {
            appliedFilter.status = IssueStatus.ASSIGNED.name
        }

    }

    override fun onResume() {
        super.onResume()
        if (AppData.isActionPerformedOnIssueDetailsPage()) {
            requestFilteredDataFromServer(appliedFilter)
        } else {
            requestDataFromServer(
                currentPageNo,
                appliedFilter
            )
        }
    }

    private fun updateAssignToMe(list: List<IssueUpdate>, isResolvePage: Boolean = false) {
        val call = apiService.updateIssues(list)
        call.enqueue(object : Callback<List<IssueResponse>> {
            override fun onResponse(
                call: Call<List<IssueResponse>>,
                response: Response<List<IssueResponse>>
            ) {
                if (response.isSuccessful) {
                    isSelectAllSelected = false
                    selectAllButton.text = resources.getText(R.string.select_all)
                    requestFilteredDataFromServer(filter = appliedFilter)

                }
            }

            override fun onFailure(call: Call<List<IssueResponse>>, t: Throwable) {
                Toast.makeText(
                    this@ViewReportedIssueAdminActivity,
                    "Failed To ${if (isResolvePage) "Resolve" else "Assign"}, Try again",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun requestFilteredDataFromServer(filter: Filter) {
        isFilterVisible = false
        appliedFilter = filter
        adapter.clearData()
        currentPageNo = 0
        requestDataFromServer(currentPageNo, filter = appliedFilter)
    }

    private fun initAdapter(context: Context, recyclerView: RecyclerView) {
        adapter = UserIssueAdapter(context, true, pageType)
        val llm = LinearLayoutManager(context)
        recyclerView.layoutManager = llm
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                requestDataFromServer(pageNo = currentPageNo, appliedFilter)
            }

        })
    }

    private fun getIssue(
        pageNo: Int,
        userId: Int,
        startDate: String,
        endDate: String,
        area: Area?,
        category: Category?,
        status: String?,
        allIssue: Boolean
    ): IssueRequest {
        val issue = IssueRequest(
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            pageNo = pageNo,
            allIssue = allIssue
        )
        if (area != null) {
            issue.areaId = area.id
        }
        if (category != null) {
            issue.categoryId = category.id
        }
        if (status != null) {
            issue.status = status
        }
        return issue
    }


    private fun requestDataFromServer(pageNo: Int, filter: Filter) {
        val startDate: String = filter.startDate
        val endDate: String = filter.endDate
        val area = getArea(areaName = filter.area?.name)
        val department =
            getCategory(categoryName = filter.department?.name)
        val status =
            getStatus(status = filter.status)
        val user = sharePrefUtil.getUser()

        progressbar.visibility = View.VISIBLE
        val issue = getIssue(
            pageNo = pageNo,
            userId = user.id,
            startDate = startDate,
            endDate = endDate,
            area = area,
            category = department,
            status = status,
            allIssue = pageType.isNullOrEmpty()
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
                progressbar.visibility = View.GONE
                if (response.isSuccessful) {
                    val data = response.body()?.issueList
                    pageCount = response.body()?.pageCount
                    adapter.updateData(data!!)
                }

            }

            override fun onFailure(call: Call<AllIssueResponse>, t: Throwable) {
                progressbar.visibility = View.GONE
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
                    filterBottomSheet.show()
                } else {
                    isFilterVisible = false
                    filterBottomSheet.dismiss()
                }
            }
        }
        return true
    }

    private fun createBottomSheetDialog(context: Context): BottomSheetDialog {
        val view = layoutInflater.inflate(R.layout.filter_issue_layout, null)
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(view)

        val areaSpinner = view.findViewById<Spinner>(R.id.filter_spinner_area)
        val categorySpinner = view.findViewById<Spinner>(R.id.filter_spinner_department)
        val statusSpinner = view.findViewById<Spinner>(R.id.filter_spinner_status)

        val etToDate = view.findViewById<EditText>(R.id.et_to_date)
        val etFromDate = view.findViewById<EditText>(R.id.et_from_date)
        val c1 = Calendar.getInstance()
        val sYear1 = c1.get(Calendar.YEAR)
        val sMonth1 = c1.get(Calendar.MONTH)
        val sDate1 = c1.get(Calendar.DAY_OF_MONTH)
        etToDate.setText("${sYear1}/" + "${sMonth1 + 1}".toTwoDigitNumber() + "/" + "$sDate1".toTwoDigitNumber())
        val c2 = Calendar.getInstance()
        c2.add(Calendar.MONTH, -1)
        val eYear2 = c2.get(Calendar.YEAR)
        val eMonth2 = c2.get(Calendar.MONTH)
        val eDate2 = c2.get(Calendar.DAY_OF_MONTH)
        etFromDate.setText("${eYear2}/" + "${eMonth2 + 1}".toTwoDigitNumber() + "/" + "$eDate2".toTwoDigitNumber())
        val areaAdapter =
            SmartCitizenSpinnerAdapter(context, AppData.getAreas().map { it.name })
        areaSpinner?.let {
            it.adapter = areaAdapter
        }

        val categoryAdapter =
            SmartCitizenSpinnerAdapter(context, AppData.getCategory().map { it.name })
        categorySpinner?.let {
            it.adapter = categoryAdapter
        }

        val statusAdapter =
            SmartCitizenSpinnerAdapter(context, AppData.getStatus(!pageType.isNullOrEmpty()))
        statusSpinner?.let {
            it.adapter = statusAdapter
        }

        view.findViewById<Button>(R.id.filter_issue_button).setOnClickListener {
            dialog.dismiss()
            val area =
                getArea(areaName = areaSpinner.selectedItem as String)
            val department =
                getCategory(categoryName = categorySpinner.selectedItem as String)
            val status =
                getStatus(status = statusSpinner.selectedItem as String)
            val f = Filter(
                startDate = etFromDate.text.toString().replace("/", ""),
                endDate = etToDate.text.toString().replace("/", ""),
                area = area,
                department = department,
                status = status
            )
            requestFilteredDataFromServer(f)
        }

        etToDate.setOnClickListener {
            val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth -> etToDate.setText("${year}/" + "${monthOfYear + 1}".toTwoDigitNumber() + "/" + "$dayOfMonth".toTwoDigitNumber()) },
                c1[Calendar.YEAR],  // Initial year selection
                c1[Calendar.MONTH],  // Initial month selection
                c1[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.maxDate = c1
            //use above calendar
            dpd.show(supportFragmentManager, "ToDatepickerdialog")
        }
        etFromDate.setOnClickListener {
            val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth -> etFromDate.setText("${year}/" + "${monthOfYear + 1}".toTwoDigitNumber() + "/" + "$dayOfMonth".toTwoDigitNumber()) },
                c2[Calendar.YEAR],  // Initial year selection
                c2[Calendar.MONTH],  // Initial month selection
                c2[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.maxDate = c1
            //use above calendar
            dpd.show(supportFragmentManager, "FromDatepickerdialog")
        }
        return dialog
    }
}