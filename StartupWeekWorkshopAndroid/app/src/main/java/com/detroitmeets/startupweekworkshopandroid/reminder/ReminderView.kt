package com.detroitmeets.startupweekworkshopandroid.reminder

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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.reminder_view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.detroitmeets.startupweekworkshopandroid.*
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import kotlinx.android.synthetic.main.content_main.*
import java.sql.Timestamp
import java.util.*


class ReminderView : Fragment() {

    private val viewModel = ReminderViewModel()
    private val adapter = ReminderListAdapter {
        reminderClicked(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.reminder_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = SharedPrefsManager(requireContext()).getCurrentUser()
        if (user.isNotEmpty()) {
            viewModel.fetchReminders(user)
        }

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
            ProgressType.NotAsked -> renderNotAsked()
            ProgressType.Loading -> {}
            ProgressType.Result -> {}
            ProgressType.Failure -> renderFailure()
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
    private fun renderNotAsked() =
        Snackbar.make(
        view!!,
        getString(R.string.add_delete_edit),
        Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()

    private fun renderFailure() =
        Snackbar.make(
            view!!,
            getString(R.string.generic_reminder_error),
            Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
}
