package com.detroitmeets.startupweekworkshopandroid.reminder

import android.view.View
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import androidx.core.content.res.ResourcesCompat
import com.airbnb.epoxy.*
import kotlinx.android.synthetic.main.content_list_item.view.*
import java.sql.Timestamp

class ReminderEpoxyWithHolder(
    private val reminder: Reminder,
    private val onClick: (Reminder) -> Unit
): EpoxyModelWithHolder<ReminderEpoxyHolder>() {

    override fun createNewHolder(): ReminderEpoxyHolder = ReminderEpoxyHolder(reminder, onClick)
    override fun getDefaultLayout(): Int = R.layout.content_list_item
}

class ReminderEpoxyHolder(val reminder: Reminder, private val onClick: (Reminder) -> Unit) : EpoxyHolder() {
    override fun bindView(itemView: View) {
        itemView.listItemTitle.text = reminder.title
        itemView.listItemDescription.text = reminder.description

        itemView.listItemTimeStamp.text = Timestamp(reminder.timestamp!!.toLong()).toString()

        if (reminder.isComplete == true) {
            itemView.listItemStatus.text = itemView.context.getString(R.string.complete)
            itemView.listItemStatus.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.lightGreen, null))
        } else {
            itemView.listItemStatus.text = itemView.context.getString(R.string.in_progress)
            itemView.listItemStatus.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.redDark, null))
        }

        itemView.setOnClickListener {
            onClick(reminder)
        }
    }
}
