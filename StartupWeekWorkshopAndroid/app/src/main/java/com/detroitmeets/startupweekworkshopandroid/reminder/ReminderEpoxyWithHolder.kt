package com.detroitmeets.startupweekworkshopandroid.reminder

import android.system.Os.bind
import android.view.View
import android.widget.TextView
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import androidx.core.content.res.ResourcesCompat
import com.airbnb.epoxy.*
import kotlinx.android.synthetic.main.content_list_item.view.*
import java.sql.Timestamp

@EpoxyModelClass(layout = R.layout.content_list_item)
class ReminderEpoxyWithHolder(
    private val reminder: Reminder,
    private val onClick: (Reminder) -> Unit
) : EpoxyModelWithHolder<ReminderEpoxyHolder>() {

    override fun getDefaultLayout(): Int = R.layout.content_list_item

    override fun createNewHolder(): ReminderEpoxyHolder = ReminderEpoxyHolder()

    @EpoxyAttribute
    lateinit var listItemTitle: String
    @EpoxyAttribute
    lateinit var listItemDescription: String
    @EpoxyAttribute
    lateinit var listItemTimeStamp: String
    @EpoxyAttribute
    lateinit var listItemTimeStatus: String
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: String

    override fun bind(holder: ReminderEpoxyHolder) {
        holder.view.listItemTitle.text = reminder.title
        holder.view.listItemDescription.text = reminder.description

        holder.view.listItemTimeStamp.text = Timestamp(reminder.timestamp!!.toLong()).toString()

        if (reminder.isComplete == true) {
            holder.view.listItemStatus.text = holder.view.context.getString(R.string.complete)
            holder.view.listItemStatus.setTextColor(
                ResourcesCompat.getColor(
                    holder.view.resources,
                    R.color.lightGreen,
                    null
                )
            )
        } else {
            holder.view.listItemStatus.text =  holder.view.context.getString(R.string.in_progress)
            holder.view.listItemStatus.setTextColor(
                ResourcesCompat.getColor(
                    holder.view.resources,
                    R.color.redDark,
                    null
                )
            )
        }

        holder.view.setOnClickListener {
            onClick(reminder)
        }
    }
}

class ReminderEpoxyHolder : EpoxyHolder() {
    lateinit var view: View
    override fun bindView(itemView: View) {
        view = itemView
    }
}
