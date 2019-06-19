package com.detroitmeets.startupweekworkshopandroid.reminder

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.detroitmeets.startupweekworkshopandroid.*
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_edit_dialog.*
import java.sql.Timestamp

class AddOrEditView(private val isEdit: Boolean, val reminder: Reminder) : Fragment() {

    private val viewModel = ReminderViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.add_edit_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val user = SharedPrefsManager(requireContext()).getCurrentUser()
        if (user.isNotEmpty()) {
            viewModel.fetchReminders(user)
        }

        if (isEdit) {
            setUpEditView()
        }

        confirmButton.setOnClickListener {
            if (isEdit) {
                viewModel.editReminder(
                    user,
                    reminder.id!!,
                    dialogTitle.text.toString(),
                    dialogDescription.text.toString(),
                    viewModel.currentViewState().markedForCompletion,
                    Timestamp(reminder.timestamp!!.toLong()).time.toDouble()
                )
            } else {
                viewModel.createReminder(
                    user,
                    reminder.id!!,
                    dialogTitle.text.toString(),
                    dialogDescription.text.toString(),
                    Timestamp(System.currentTimeMillis()).time.toDouble()
                )
            }
            fragmentManager?.popBackStack()
        }

        cancelButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }


        viewModel.viewState.observe(this, Observer<ReminderViewModel.ReminderViewState> {
            if (it.markedForDeletion) {
                viewModel.deleteReminder(user, reminder)
            }
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

    private fun setUpEditView() {
        editToolbar.title = getString(R.string.edit_reminder)
        statusButton.visibility = VISIBLE
        deleteReminder.visibility = VISIBLE
        dialogTimestamp.visibility = VISIBLE

        dialogTitle.setText(reminder.title)
        dialogDescription.setText(reminder.description)
        dialogTimestamp.text = Timestamp(reminder.timestamp!!.toLong()).toString()

        if (reminder.isComplete == true) {
            statusButton.text = getString(R.string.complete)
            statusButton.setTextColor(ResourcesCompat.getColor(resources, R.color.lightGreen, null))
        } else {
            statusButton.text = getString(R.string.in_progress)
            ResourcesCompat.getColor(resources, R.color.redDark, null)
        }

        statusButton.setOnClickListener {
            if (statusButton.text.toString().equals(getString(R.string.in_progress), ignoreCase = true)) {
                statusButton.text = getString(R.string.complete)
                statusButton.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.lightGreen,
                        null
                    )
                )
                viewModel.toggleMarkedForCompletion(true)
            } else {
                statusButton.text = getString(R.string.in_progress)
                statusButton.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.redDark,
                        null
                    )
                )
                viewModel.toggleMarkedForDeletion(false)
            }
        }

        deleteReminder.setOnClickListener {
            showConfirmDeleteDialog()
        }
    }

    private fun showConfirmDeleteDialog() {
        val confirmDialog = AlertDialog.Builder(context)
        confirmDialog.setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.toggleMarkedForDeletion(true)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                viewModel.toggleMarkedForDeletion(false)
                dialog.cancel()
            }.show().setOnDismissListener {
                if (viewModel.currentViewState().markedForDeletion) {
                    viewModel.deleteReminder(user?.uid!!, reminder)
                    fragmentManager?.popBackStack()
                }
            }
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