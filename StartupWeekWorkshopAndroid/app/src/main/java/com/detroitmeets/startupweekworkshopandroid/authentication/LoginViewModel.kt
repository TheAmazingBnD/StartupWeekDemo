package com.detroitmeets.startupweekworkshopandroid.authentication

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.detroitmeets.startupweekworkshopandroid.*
import com.detroitmeets.startupweekworkshopandroid.api.models.User
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot

class LoginViewModel : ViewModel() {
    val viewState: MutableLiveData<LoginViewState> = MutableLiveData()

    init {
        viewState.value = LoginViewState(
            progressType = ProgressType.NotAsked,
            isValidated = false,
            userUID = ""
        )
    }

    fun currentViewState(): LoginViewState = viewState.value!!

    fun validateInput(input: String) {
        if (input.isNotEmpty()) {
            updateState(
                LoginViewState(
                    progressType = currentViewState().progressType,
                    isValidated = true,
                    userUID = currentViewState().userUID
                )
            )
        }
    }

    fun postLogin(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            updateState(
                LoginViewState(
                    progressType = ProgressType.Loading,
                    isValidated = currentViewState().isValidated,
                    userUID = currentViewState().userUID
                )
            )
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {

                    // Assert Non null here because we had a
                    // successful login as per result. **There are better ways**
                    val authUser = auth.currentUser!!

                    db.reference.child("Users").child(authUser.uid).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val data = dataSnapshot.getValue(User::class.java)

                                if(data != null) {
                                    user = user?.copy(
                                        uid = authUser.uid,
                                        email = data.email,
                                        firstName = data.firstName,
                                        lastName = data.lastName
                                    )
                                }

                                updateState(
                                    LoginViewState(
                                        progressType = ProgressType.Result,
                                        isValidated = currentViewState().isValidated,
                                        userUID = authUser.uid
                                    )
                                )
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                updateState(
                                    LoginViewState(
                                        progressType = ProgressType.Failure,
                                        isValidated = currentViewState().isValidated,
                                        userUID = currentViewState().userUID
                                    )
                                )
                            }
                        }
                    )
                } else {
                    updateState(
                        LoginViewState(
                            progressType = ProgressType.Failure,
                            isValidated = currentViewState().isValidated,
                            userUID = currentViewState().userUID
                        )
                    )
                }
            }
        } else {
            updateState(
                LoginViewState(
                    progressType = ProgressType.Failure,
                    isValidated = currentViewState().isValidated,
                    userUID = currentViewState().userUID
                )
            )
        }
    }

    private fun updateState(newState: LoginViewState) {
        viewState.value =
            currentViewState().copy(
                progressType = newState.progressType,
                isValidated = newState.isValidated,
                userUID = newState.userUID
            )
    }

    data class LoginViewState(val progressType: ProgressType, val isValidated: Boolean, val userUID: String)
}
