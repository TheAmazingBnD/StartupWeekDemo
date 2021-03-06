package com.detroitmeets.startupweekworkshopandroid.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.detroitmeets.startupweekworkshopandroid.ProgressType
import com.detroitmeets.startupweekworkshopandroid.api.models.User
import com.detroitmeets.startupweekworkshopandroid.auth
import com.detroitmeets.startupweekworkshopandroid.db
import java.util.*

class SignUpViewModel : ViewModel() {

    val viewState: MutableLiveData<SignUpViewState> = MutableLiveData()

    init {
        viewState.value = SignUpViewState(progressType = ProgressType.NotAsked, isValidated = false)
    }

    fun validateInput(input: String) {
        if (input.isNotEmpty()) {
            updateState(SignUpViewState(progressType = currentViewState().progressType, isValidated = true))
        }
    }

    fun createNewUser(email: String, password: String, firstName: String, lastName: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
            updateState(
                SignUpViewState(
                    progressType = ProgressType.Loading,
                    isValidated = currentViewState().isValidated
                )
            )
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Assert Non null here because we had a
                    // successful login as per result. **There are better ways**
                    val user = auth.currentUser!!
                    db.reference.child("Users").child(user.uid).setValue(
                        hashMapOf<String, Any>(
                            Pair("email", email),
                            Pair("firstName", firstName),
                            Pair("lastName", lastName)
                        )
                    )

                    updateState(
                        SignUpViewState(
                            progressType = ProgressType.Result,
                            isValidated = currentViewState().isValidated
                        )
                    )
                } else {
                    updateState(
                        SignUpViewState(
                            progressType = ProgressType.Failure,
                            isValidated = currentViewState().isValidated
                        )
                    )
                }
            }
        } else {
            updateState(
                SignUpViewState(
                    progressType = ProgressType.Failure,
                    isValidated = currentViewState().isValidated
                )
            )
        }
    }

    private fun currentViewState(): SignUpViewState = viewState.value!!

    private fun updateState(newState: SignUpViewState) {
        viewState.value = currentViewState().copy(
            progressType = newState.progressType,
            isValidated = newState.isValidated
        )
    }

    data class SignUpViewState(val progressType: ProgressType, val isValidated: Boolean)
}