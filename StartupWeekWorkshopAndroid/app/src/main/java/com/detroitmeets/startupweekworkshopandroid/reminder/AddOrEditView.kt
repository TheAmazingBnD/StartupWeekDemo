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
import java.util.*

class AddOrEditView(val isEdit: Boolean, val reminder: Reminder) : Fragment() {

    private val viewModel = ReminderViewModel(user?.uid.orEmpty())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.add_edit_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEdit) {
            setUpEditView()
        }

        confirmButton.setOnClickListener {
            if (isEdit) {
                viewModel.editReminder(
                    user?.uid!!,
                    reminder.id!!,
                    dialogTitle.text.toString(),
                    dialogDescription.text.toString(),
                    viewModel.currentViewState().markedForCompletion,
                    Timestamp(reminder.timestamp!!.toLong()).time.toDouble()
                )
            } else {
                viewModel.createReminder(
                    user?.uid!!,
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
                viewModel.deleteReminder(user?.uid!!, reminder)
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
            ProgressType.NotAsked -> {
            }
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

    private fun setUpEditView() {
        editToolbar.title = "Edit Reminder"
        statusButton.visibility = VISIBLE
        deleteReminder.visibility = VISIBLE
        dialogTimestamp.visibility = VISIBLE

        dialogTitle.setText(reminder.title)
        dialogDescription.setText(reminder.description)
        dialogTimestamp.text = Timestamp(reminder.timestamp!!.toLong()).toString()

        if (reminder.isComplete == true) {
            statusButton.text = "Completed"
            statusButton.setTextColor(ResourcesCompat.getColor(resources, R.color.lightGreen, null))
        } else {
            statusButton.text = "In Progress"
            ResourcesCompat.getColor(resources, R.color.redDark, null)
        }

        statusButton.setOnClickListener {
            if (statusButton.text.toString().equals("In Progress", ignoreCase = true)) {
                statusButton.text = "Completed"
                statusButton.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.lightGreen,
                        null
                    )
                )
                viewModel.toggleMarkedForCompletion(true)
            } else {
                statusButton.text = "In Progress"
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
        val confrimDialog = AlertDialog.Builder(context)
        confrimDialog.setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Ok") { dialog, which ->
                viewModel.toggleMarkedForDeletion(true)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
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
}