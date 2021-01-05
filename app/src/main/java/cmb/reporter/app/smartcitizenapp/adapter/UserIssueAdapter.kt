package cmb.reporter.app.smartcitizenapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import cmb.reporter.app.smartcitizenapp.AppData
import cmb.reporter.app.smartcitizenapp.R
import cmb.reporter.app.smartcitizenapp.activity.IssueDetailsActivity
import cmb.reporter.app.smartcitizenapp.models.IssueResponse
import cmb.reporter.app.smartcitizenapp.models.IssueStatus
import cmb.reporter.app.smartcitizenapp.models.IssueUpdate
import cmb.reporter.app.smartcitizenapp.sharedPref.SharePrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat


class UserIssueAdapter(
    private val context: Context,
    private val isAdminView: Boolean,
    private val pageType: String?,
    val updateCountFunction: (count: String) -> Unit
) :
    RecyclerView.Adapter<UserIssueAdapter.ViewHolder>() {
    private var list: MutableList<IssueResponse> = mutableListOf()
    private val user = SharePrefUtil(context).getUser()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            if (isAdminView) R.layout.issue_row_admin_layout else R.layout.issue_row_user_layout,
            parent,
            false
        )
        return ViewHolder(v, isAdminView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = list[position]
        holder.bindItems(context, issue)
        holder.itemView.setOnClickListener {
            AppData.setSelectedIssue(issue)
            val intent = Intent(context, IssueDetailsActivity::class.java)
            context.startActivity(intent)
        }
        if (isAdminView) {
            holder.setIsRecyclable(false)
            val checkBox = holder.itemView.findViewById<AppCompatCheckBox>(R.id.row_checkBox)
            checkBox.setOnClickListener {
                val state = (it as AppCompatCheckBox).isChecked
                issue.isSelected = state
            }
            if (!pageType.isNullOrEmpty()) {
                if (issue.status == IssueStatus.OPEN.name || issue.status == IssueStatus.REJECTED.name || issue.status == IssueStatus.RESOLVED.name) {
                    checkBox.visibility = View.INVISIBLE
                } else if (issue.status == IssueStatus.ASSIGNED.name) {
                    checkBox.visibility = View.VISIBLE
                }
            } else {
                if (issue.status == IssueStatus.REJECTED.name || issue.status == IssueStatus.RESOLVED.name || issue.status == IssueStatus.ASSIGNED.name) {
                    checkBox.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun getSelectedItems(isResolvedList: Boolean = false): List<IssueUpdate> {
        val updateList = mutableListOf<IssueUpdate>()
        list.forEach {
            if (it.isSelected && !isResolvedList && it.status == IssueStatus.OPEN.name) {
                updateList.add(IssueUpdate(it.id.toLong(), IssueStatus.ASSIGNED.name, user, user))
            } else if (it.isSelected && isResolvedList && it.status == IssueStatus.ASSIGNED.name) {
                updateList.add(IssueUpdate(it.id.toLong(), IssueStatus.RESOLVED.name, null, null))
            }
        }
        return updateList
    }

    fun updateData(data: List<IssueResponse>) {
        list.addAll(data)
        notifyDataSetChanged()
    }

    fun changeSelectedState(selectAll: Boolean, isResolvedList: Boolean = false) {
        list.forEach {
            if (isResolvedList) {
                if (it.status == IssueStatus.ASSIGNED.name ) {
                    it.isSelected = selectAll
                }
            } else {
                if (it.status == IssueStatus.OPEN.name ) {
                    it.isSelected = selectAll
                }
            }
        }
        notifyDataSetChanged()
    }

    fun clearData() {
        list = mutableListOf()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View, private val isAdminView: Boolean) :
        RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, issue: IssueResponse) {
            val image = itemView.findViewById(R.id.row_imageView) as ImageView
            val area = itemView.findViewById(R.id.row_area_textView) as TextView
            val description = itemView.findViewById(R.id.row_description_textView) as TextView
            val date = itemView.findViewById(R.id.row_date_textView) as TextView
            val status = itemView.findViewById(R.id.row_status_textView) as TextView
            if (isAdminView) {
                val checkBox = itemView.findViewById(R.id.row_checkBox) as CheckBox
                checkBox.isChecked = issue.isSelected
            }
            image.setImageViaGlide(context, "http://95.111.198.176:9001${issue.imageUrl[0]}")
            area.text = issue.area?.name
            description.text = issue.description
            date.text = convertDateToReadableFormat(issue.createdDate)
            status.text = issue.status
        }
    }
}

fun convertDateToReadableFormat(date: String): String {
    val dateSubString = date.substring(0, 19)
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return formatter.format(parser.parse(dateSubString))
}

fun ImageView.setImageViaGlide(
    context: Context,
    url: String,
    defaultImage: Int = R.drawable.issue_location
) {
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.issue_location)
        .error(R.drawable.issue_location)
        .centerCrop()
    Glide.with(context).load(url).apply(options).into(this)
}

fun ImageView.setImageViaGlide(
    context: Context,
    imageDrawable: Int = R.drawable.logo
) {
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.logo)
        .error(R.drawable.logo).fitCenter()
    Glide.with(context).load(imageDrawable).apply(options).into(this)
}