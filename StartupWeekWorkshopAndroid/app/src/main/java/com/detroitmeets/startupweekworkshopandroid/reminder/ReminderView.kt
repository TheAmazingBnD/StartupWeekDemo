package com.detroitmeets.startupweekworkshopandroid.reminder

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.detroitmeets.startupweekworkshopandroid.ProgressType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.reminder_view.*
import android.view.View.VISIBLE
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.detroitmeets.startupweekworkshopandroid.MainActivity
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import com.detroitmeets.startupweekworkshopandroid.user
import kotlinx.android.synthetic.main.add_edit_dialog.*
import kotlinx.android.synthetic.main.add_edit_dialog.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.sql.Timestamp
import java.util.*


class ReminderView : Fragment() {

    private val viewModel = ReminderViewModel(user?.uid.orEmpty())
    private val adapter = ReminderListAdapter {
        reminderClicked(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.reminder_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchReminders(user?.uid!!)

        fabAddToDo.setOnClickListener {
            MainActivity().addFragmentToActivity(
                fragmentManager,
                AddOrEditView(
                    false,
                    Reminder(
                        UUID.randomUUID().toString(),
                        "",
                        "",
                        false,
                        Timestamp(System.currentTimeMillis()).time.toDouble()
                    )
                ), R.id.mainActivity
            )
        }


        reminderRecycler.layoutManager = LinearLayoutManager(context)

        reminderRecycler.adapter = adapter

        viewModel.viewState.observe(this, Observer<ReminderViewModel.ReminderViewState> {
            adapter.loadItems(it.reminders ?: emptyList())
            render(it)
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }


    private fun render(viewState: ReminderViewModel.ReminderViewState) {
        when (viewState.progressType) {
            ProgressType.NotAsked -> {}
//                Snackbar.make(view!!, "Please input and confirm informaion", Snackbar.LENGTH_SHORT).setAction("Okey") {
//
//                }.show()
            ProgressType.Loading -> {
            }
            ProgressType.Result -> {
//                MainActivity().addFragmentToActivity(fragmentManager, ReminderView(), R.id.mainActivity)
            }
            ProgressType.Failure -> {
                Snackbar.make(view!!, "Error", Snackbar.LENGTH_SHORT).setAction("Okey") {

                }.show()
            }
        }
    }

    private fun reminderClicked(reminder: Reminder) {
        MainActivity().addFragmentToActivity(fragmentManager, AddOrEditView(true, reminder), R.id.mainActivity)
    }

    private fun setTextWatcher(editText: EditText) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(sequence: Editable?) {
                viewModel.validateInput(sequence.toString())
            }

            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        editText.addTextChangedListener(textWatcher)
    }

}
