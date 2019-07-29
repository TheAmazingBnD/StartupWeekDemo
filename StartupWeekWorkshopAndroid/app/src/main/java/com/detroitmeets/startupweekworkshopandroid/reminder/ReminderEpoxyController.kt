package com.detroitmeets.startupweekworkshopandroid.reminder

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.airbnb.epoxy.TypedEpoxyController
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import kotlinx.android.synthetic.main.content_list_item.view.*

class ReminderEpoxyController(private val onClick: (Reminder) -> Unit) : TypedEpoxyController<List<Reminder>>() {

    override fun buildModels(data: List<Reminder>) {
        data.forEach {
            ReminderEpoxyModel(it, onClick).id(it.timestamp).addTo(this)
//            ReminderEpoxyWithHolder(it, onClick).id(it.timestamp).addTo(this)
        }
    }
}
