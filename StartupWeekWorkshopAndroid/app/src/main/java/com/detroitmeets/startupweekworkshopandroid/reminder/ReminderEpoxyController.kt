package com.detroitmeets.startupweekworkshopandroid.reminder

import com.airbnb.epoxy.TypedEpoxyController
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder

class ReminderEpoxyController(private val onClick: (Reminder) -> Unit) : TypedEpoxyController<List<Reminder>>() {

    override fun buildModels(data: List<Reminder>) {
        data.forEach {
//            ReminderEpoxyModel(it, onClick).id(it.timestamp).addTo(this)
            ReminderEpoxyWithHolder(it, onClick).id(it.timestamp).addTo(this)
        }

    }
}
