package cmb.reporter.smartcitizen.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cmb.reporter.smartcitizen.AppData
import cmb.reporter.smartcitizen.R
import cmb.reporter.smartcitizen.activity.IssueDetailsActivity
import cmb.reporter.smartcitizen.models.IssueResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.lang.Appendable


class UserIssueAdapter(val context: Context, val list: List<IssueResponse>) :
    RecyclerView.Adapter<UserIssueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserIssueAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.issue_row_user_layout,
            parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = list[position]
        holder.bindItems(context, issue)
        holder.itemView.setOnClickListener {
            AppData.setSelectedIssue(issue)
            val intent = Intent(context, IssueDetailsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, issue: IssueResponse) {
            val image = itemView.findViewById(R.id.row_imageView) as ImageView
            val area = itemView.findViewById(R.id.row_area_textView) as TextView
            val description = itemView.findViewById(R.id.row_description_textView) as TextView
            val date = itemView.findViewById(R.id.row_date_textView) as TextView
            val status = itemView.findViewById(R.id.row_status_textView) as TextView

            image.setImageViaGlide(context, "http://95.111.198.176:9001${issue.imageUrl[0]}")
            area.text = issue.area?.name
            description.text = issue.description
            date.text = issue.createdDate
            status.text = issue.status
        }
    }
}

fun ImageView.setImageViaGlide(context: Context, url: String, defaultImage: Int = R.drawable.issue_location){
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.issue_location)
        .error(R.drawable.issue_location)
    Glide.with(context).load(url).apply(options).into(this)
}