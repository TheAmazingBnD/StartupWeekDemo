package com.detroitmeets.startupweekworkshopandroid.authentication

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
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

        loginButton.setOnClickListener {
            viewModel.login(
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
            ProgressType.NotAsked -> renderNotAsked()
            ProgressType.Loading -> loginProgressBar.visibility = VISIBLE
            ProgressType.Result -> renderResult()
            ProgressType.Failure -> renderFailure()
        }
    }

    private fun renderNotAsked() = Snackbar.make(
        view!!,
        getString(R.string.please_confirm),
        Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()

    private fun renderResult() {
        if (loginProgressBar.visibility == VISIBLE) {
            loginProgressBar.visibility = View.GONE
        }
        SharedPrefsManager(requireContext()).setCurrentUser(viewModel.currentViewState().userUID)
        MainActivity().addFragmentToActivity(fragmentManager, ReminderView(), R.id.mainActivity)
    }

    private fun renderFailure() {
        Snackbar.make(
            view!!,
            getString(R.string.error_logging_in),
            Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
        if (loginProgressBar.visibility == VISIBLE) {
            loginProgressBar.visibility = View.GONE
        }
    }
}
