package cmb.reporter.app.smartcitizenapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cmb.reporter.app.smartcitizenapp.R


class SmartCitizenSpinnerAdapter(private val context: Context, private val list: List<String>) :
    BaseAdapter() {
    var inflter: LayoutInflater = (LayoutInflater.from(context))
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = inflter.inflate(R.layout.string_adapter_row, null)
        val tv = convertView.findViewById<TextView>(R.id.string_row)
        tv.text = list[position]
        if (position != 0) {
            tv.setTextColor(context.resources.getColor(R.color.colorAccent))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
        }
        return tv
    }

}