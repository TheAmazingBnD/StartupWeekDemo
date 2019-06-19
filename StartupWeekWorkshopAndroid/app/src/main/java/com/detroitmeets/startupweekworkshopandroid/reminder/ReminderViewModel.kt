package com.detroitmeets.startupweekworkshopandroid.reminder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.detroitmeets.startupweekworkshopandroid.MainActivity
import com.detroitmeets.startupweekworkshopandroid.ProgressType
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import com.detroitmeets.startupweekworkshopandroid.authentication.LoginViewModel
import com.detroitmeets.startupweekworkshopandroid.db
import com.detroitmeets.startupweekworkshopandroid.user
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.sql.Timestamp
import java.util.*

class ReminderViewModel : ViewModel() {
    val viewState: MutableLiveData<ReminderViewState> = MutableLiveData()

    init {
        viewState.value = ReminderViewState(
            progressType = ProgressType.NotAsked,
            isValidated = false,
            reminders = mutableListOf(),
            markedForDeletion = false,
            markedForCompletion = false,
            userUID = user?.uid.orEmpty()
        )
    }

    fun currentViewState(): ReminderViewState = viewState.value!!

    fun validateInput(input: String) {
        if (input.isNotEmpty()) {
            updateState(
                ReminderViewState(
                    progressType = currentViewState().progressType,
                    isValidated = true,
                    reminders = currentViewState().reminders,
                    markedForDeletion = currentViewState().markedForDeletion,
                    markedForCompletion = currentViewState().markedForCompletion,
                    userUID = currentViewState().userUID
                )
            )
        }
    }

    fun fetchReminders(uid: String) {
        if (uid.isNotEmpty()) {
            updateState(
                ReminderViewState(
                    progressType = ProgressType.Loading,
                    isValidated = currentViewState().isValidated,
                    reminders = currentViewState().reminders,
                    markedForDeletion = currentViewState().markedForDeletion,
                    markedForCompletion = currentViewState().markedForCompletion,
                    userUID = currentViewState().userUID
                )
            )
            db.reference.child("Reminders").child(uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.children

                        val reminders = mutableListOf<Reminder>()



                        for (child in data) {
                            val newReminder = child.getValue(Reminder::class.java)

                            if (newReminder != null) {
                                reminders.add(newReminder)
                            }
                        }

                        reminders.sortBy { it.timestamp }

                        updateState(
                            ReminderViewState(
                                progressType = ProgressType.Result,
                                reminders = reminders,
                                isValidated = currentViewState().isValidated,
                                markedForDeletion = currentViewState().markedForDeletion,
                                markedForCompletion = currentViewState().markedForCompletion,
                                userUID = currentViewState().userUID
                            )
                        )
                    }


                    override fun onCancelled(databaseError: DatabaseError) {
                        updateState(
                            ReminderViewState(
                                progressType = ProgressType.Failure,
                                reminders = currentViewState().reminders,
                                isValidated = currentViewState().isValidated,
                                markedForDeletion = currentViewState().markedForDeletion,
                                markedForCompletion = currentViewState().markedForCompletion,
                                userUID = currentViewState().userUID
                            )
                        )
                    }
                }
            )
        } else {
            updateState(
                ReminderViewState(
                    progressType = ProgressType.Failure,
                    reminders = currentViewState().reminders,
                    isValidated = currentViewState().isValidated,
                    markedForDeletion = currentViewState().markedForDeletion,
                    markedForCompletion = currentViewState().markedForCompletion,
                    userUID = currentViewState().userUID
                )
            )
        }
    }

    fun createReminder(uid: String, reminderID: String, title: String, description: String, timeStamp: Double) {
        val reminders = currentViewState().reminders

        updateState(
            ReminderViewState(
                progressType = ProgressType.Loading,
                isValidated = currentViewState().isValidated,
                reminders = reminders,
                markedForDeletion = currentViewState().markedForDeletion,
                markedForCompletion = currentViewState().markedForCompletion,
                userUID = currentViewState().userUID
            )
        )

        db.reference.child("Reminders").child(uid).child(reminderID).setValue(
            Reminder(
                reminderID,
                title = title,
                description = description,
                isComplete = false,
                timestamp = timeStamp
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                updateState(
                    ReminderViewState(
                        progressType = ProgressType.Result,
                        reminders = reminders,
                        isValidated = currentViewState().isValidated,
                        markedForDeletion = currentViewState().markedForDeletion,
                        markedForCompletion = currentViewState().markedForCompletion,
                        userUID = currentViewState().userUID
                    )
                )

                fetchReminders(uid)

            } else {
                updateState(
                    ReminderViewState(
                        progressType = ProgressType.Failure,
                        reminders = currentViewState().reminders,
                        isValidated = currentViewState().isValidated,
                        markedForDeletion = currentViewState().markedForDeletion,
                        markedForCompletion = currentViewState().markedForCompletion,
                        userUID = currentViewState().userUID
                    )
                )
            }
        }
    }

    fun editReminder(
        uid: String,
        reminderID: String,
        title: String,
        description: String,
        isComplete: Boolean,
        timeStamp: Double
    ) {
        val reminders = currentViewState().reminders

        updateState(
            ReminderViewState(
                progressType = ProgressType.Loading,
                isValidated = currentViewState().isValidated,
                reminders = reminders,
                markedForDeletion = currentViewState().markedForDeletion,
                markedForCompletion = currentViewState().markedForCompletion,
                userUID = currentViewState().userUID
            )
        )

        db.reference.child("Reminders").child(uid).child(reminderID).setValue(
            Reminder(
                reminderID,
                title = title,
                description = description,
                isComplete = isComplete,
                timestamp = timeStamp
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                updateState(
                    ReminderViewState(
                        progressType = ProgressType.Result,
                        reminders = reminders,
                        isValidated = currentViewState().isValidated,
                        markedForDeletion = currentViewState().markedForDeletion,
                        markedForCompletion = currentViewState().markedForCompletion,
                        userUID = currentViewState().userUID
                    )
                )

                fetchReminders(uid)

            } else {
                updateState(
                    ReminderViewState(
                        progressType = ProgressType.Failure,
                        reminders = currentViewState().reminders,
                        isValidated = currentViewState().isValidated,
                        markedForDeletion = currentViewState().markedForDeletion,
                        markedForCompletion = currentViewState().markedForCompletion,
                        userUID = currentViewState().userUID
                    )
                )
            }
        }
    }

    fun deleteReminder(uid: String, reminder: Reminder) {
        if (uid.isEmpty()) {
            db.reference.child("Reminders").child(uid).child(reminder.id!!).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {

                    updateState(
                        ReminderViewState(
                            progressType = ProgressType.Result,
                            reminders = currentViewState().reminders,
                            isValidated = currentViewState().isValidated,
                            markedForDeletion = currentViewState().markedForDeletion,
                            markedForCompletion = currentViewState().markedForCompletion,
                            userUID = currentViewState().userUID
                        )
                    )

                    fetchReminders(uid)

                } else {
                    updateState(
                        ReminderViewState(
                            progressType = ProgressType.Failure,
                            reminders = currentViewState().reminders,
                            isValidated = currentViewState().isValidated,
                            markedForDeletion = currentViewState().markedForDeletion,
                            markedForCompletion = currentViewState().markedForCompletion,
                            userUID = currentViewState().userUID
                        )
                    )
                }
            }
        }
    }

    fun toggleMarkedForDeletion(mark: Boolean) {
        updateState(
            currentViewState().copy(
                progressType = currentViewState().progressType,
                isValidated = currentViewState().isValidated,
                reminders = currentViewState().reminders,
                markedForDeletion = mark,
                markedForCompletion = currentViewState().markedForCompletion,
                userUID = currentViewState().userUID
            )
        )
    }

    fun toggleMarkedForCompletion(mark: Boolean) {
        updateState(
            currentViewState().copy(
                progressType = currentViewState().progressType,
                isValidated = currentViewState().isValidated,
                reminders = currentViewState().reminders,
                markedForDeletion = currentViewState().markedForDeletion,
                markedForCompletion = mark,
                userUID = currentViewState().userUID
            )
        )
    }

    private fun updateState(newState: ReminderViewState) {
        viewState.value = currentViewState()
            .copy(
                progressType = newState.progressType,
                isValidated = newState.isValidated,
                reminders = newState.reminders,
                markedForDeletion = newState.markedForDeletion,
                markedForCompletion = newState.markedForCompletion,
                userUID = newState.userUID
            )
    }

    data class ReminderViewState(
        val progressType: ProgressType,
        val isValidated: Boolean,
        val reminders: MutableList<Reminder>?,
        val markedForDeletion: Boolean,
        val markedForCompletion: Boolean,
        val userUID: String
    )

}
