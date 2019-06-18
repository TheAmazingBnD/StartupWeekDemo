package com.detroitmeets.startupweekworkshopandroid.authentication

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.detroitmeets.startupweekworkshopandroid.*
import com.detroitmeets.startupweekworkshopandroid.reminder.ReminderView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.signup_view.*


class SignUpView : Fragment() {

    private val viewModel = SignUpViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.signup_view, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setTextWatcher(signUpFirstName)
//        setTextWatcher(signUpLastName)
//        setTextWatcher(signUpEmail)
//        setTextWatcher(signUpPassword)
//        setTextWatcher(signUpConfirmPassword)

        loginTV.setOnClickListener {
            MainActivity().addFragmentToActivity(fragmentManager, LoginView(), R.id.mainActivity )
        }

        confirmSignUpButton.setOnClickListener {
            viewModel.postSignUp(
                signUpEmail.text.toString(),
                signUpPassword.text.toString(),
                signUpFirstName.text.toString(),
                signUpLastName.text.toString())
        }

        viewModel.viewState.observe(this, Observer<SignUpViewModel.SignUpViewState> {
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

    private fun render(viewState: SignUpViewModel.SignUpViewState) {
        when (viewState.progressType) {
            ProgressType.NotAsked ->
                Snackbar.make(view!!, "Please input and confirm informaion", Snackbar.LENGTH_SHORT).setAction("Okey") {

                }.show()
            ProgressType.Loading -> signUpProgressBar.visibility = VISIBLE
            ProgressType.Result -> {
                if (signUpProgressBar.visibility == VISIBLE) {
                    signUpProgressBar.visibility = GONE
                }

                MainActivity().addFragmentToActivity(fragmentManager, ReminderView(), R.id.mainActivity)
            }
            ProgressType.Failure -> {

            }
        }

//        if (viewState.isValidated) {
//            confirmSignUpButton.alpha = 1f
//            confirmSignUpButton.isEnabled = true
//        } else {
//            confirmSignUpButton.alpha = 0.5f
//            confirmSignUpButton.isEnabled = false
//        }
    }

    private fun renderNotAsked() {

    }

    private fun renderLoading() {

    }

    private fun rederResult() {

    }

    private fun rederFailure() {

    }
}
