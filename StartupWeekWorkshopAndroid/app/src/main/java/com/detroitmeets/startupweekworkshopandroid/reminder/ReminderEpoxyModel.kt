package com.detroitmeets.startupweekworkshopandroid.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.airbnb.epoxy.EpoxyModelWithView
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import kotlinx.android.synthetic.main.content_list_item.view.*
import java.sql.Timestamp

data class ReminderEpoxyModel(private val reminder: Reminder, private val onClick: (Reminder) -> Unit) : EpoxyModelWithView<ConstraintLayout>() {
    override fun bind(view: ConstraintLayout) {
        super.bind(view)
        view.listItemTitle.text = reminder.title
        view.listItemDescription.text = reminder.description

        view.listItemTimeStamp.text = Timestamp(reminder.timestamp!!.toLong()).toString()

        if (reminder.isComplete == true) {
            view.listItemStatus.text = view.context.getString(R.string.complete)
            view.listItemStatus.setTextColor(ResourcesCompat.getColor(view.resources, R.color.lightGreen, null))
        } else {
            view.listItemStatus.text = view.context.getString(R.string.in_progress)
            view.listItemStatus.setTextColor(ResourcesCompat.getColor(view.resources, R.color.redDark, null))
        }

        view.setOnClickListener {
            onClick(reminder)
        }
    }

    override fun buildView(parent: ViewGroup): ConstraintLayout =
        LayoutInflater.from(parent.context).inflate(
            R.layout.content_list_item,
            parent,
            false
        ) as ConstraintLayout
}
