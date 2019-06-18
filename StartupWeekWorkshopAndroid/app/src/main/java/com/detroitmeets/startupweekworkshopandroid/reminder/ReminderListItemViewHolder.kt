package com.detroitmeets.startupweekworkshopandroid.reminder

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import kotlinx.android.synthetic.main.content_list_item.view.*
import java.sql.Timestamp

class ReminderListItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(newValue: Reminder) {
        view.listItemTitle.text = newValue.title
        view.listItemDescription.text = newValue.description
        view.listItemTimeStamp.text = Timestamp(newValue.timestamp!!.toLong()).toString()
        if (newValue.isComplete == true) {
            view.listItemStatus.text = "Complete"
            view.listItemStatus.setTextColor(ResourcesCompat.getColor(view.resources, R.color.lightGreen, null))
        } else {
            view.listItemStatus.text = "In Progress"
            view.listItemStatus.setTextColor(ResourcesCompat.getColor(view.resources, R.color.redDark, null))
        }
    }
}
