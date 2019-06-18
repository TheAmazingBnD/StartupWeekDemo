package com.detroitmeets.startupweekworkshopandroid.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder

class ReminderListAdapter(val onClick: (Reminder) -> Unit) : RecyclerView.Adapter<ReminderListItemViewHolder>() {
    private var items: List<Reminder> = emptyList()

    fun loadItems(newItems: List<Reminder>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListItemViewHolder
            = ReminderListItemViewHolder(
        LayoutInflater.from(parent.context)
        .inflate(R.layout.content_list_item, parent, false))


    override fun onBindViewHolder(holder: ReminderListItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.view.setOnClickListener { onClick(items[position]) }
    }
}