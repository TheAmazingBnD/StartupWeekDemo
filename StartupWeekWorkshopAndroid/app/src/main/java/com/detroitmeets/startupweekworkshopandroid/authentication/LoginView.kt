package com.detroitmeets.startupweekworkshopandroid.authentication

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.detroitmeets.startupweekworkshopandroid.MainActivity
import com.detroitmeets.startupweekworkshopandroid.ProgressType
import com.detroitmeets.startupweekworkshopandroid.R
import com.detroitmeets.startupweekworkshopandroid.SharedPrefsManager
import com.detroitmeets.startupweekworkshopandroid.reminder.ReminderView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_view.*

class LoginView : Fragment() {

    private val viewModel = LoginViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.login_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setTextWatcher(loginEmail)
//        setTextWatcher(loginPassword)

        loginButton.setOnClickListener {
            viewModel.postLogin(
                loginEmail.text.toString(),
                loginPassword.text.toString()
            )
        }

        viewModel.viewState.observe(this, Observer<LoginViewModel.LoginViewState> {
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


    private fun render(viewState: LoginViewModel.LoginViewState) {
        when (viewState.progressType) {
            ProgressType.NotAsked -> {}
//                Snackbar.make(view!!, "Please input and confirm informaion", Snackbar.LENGTH_SHORT).setAction("Okey") {
//
//                }.show()
            ProgressType.Loading -> { loginProgressBar.visibility = VISIBLE }
            ProgressType.Result -> {
                if (loginProgressBar.visibility == VISIBLE) {
                    loginProgressBar.visibility = View.GONE
                }

                SharedPrefsManager(requireContext()).setCurrentUser(viewModel.currentViewState().userUID)
                MainActivity().addFragmentToActivity(fragmentManager, ReminderView(), R.id.mainActivity)
            }
            ProgressType.Failure -> {
                Snackbar.make(view!!, "Error Logging In", Snackbar.LENGTH_SHORT).setAction("Okey") {

                }.show()
                if (loginProgressBar.visibility == VISIBLE) {
                    loginProgressBar.visibility = View.GONE
                }
            }
        }

        //        if (viewState.isValidated) {
        //            loginButton.alpha = 1f
        //            loginButton.isEnabled = true
        //        } else {
        //            loginButton.alpha = 0.5f
        //            loginButton.isEnabled = false
        //        }
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

    private fun renderNotAsked() {

    }

    private fun renderLoading() {

    }

    private fun rederResult() {

    }

    private fun rederFailure() {

    }

}
