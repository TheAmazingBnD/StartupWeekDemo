package com.detroitmeets.startupweekworkshopandroid

import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.detroitmeets.startupweekworkshopandroid.api.models.User
import com.detroitmeets.startupweekworkshopandroid.authentication.LoginView
import com.detroitmeets.startupweekworkshopandroid.authentication.SignUpView
import com.detroitmeets.startupweekworkshopandroid.reminder.ReminderView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_main.*


// Global variables at top level ***not best practice***
val auth = FirebaseAuth.getInstance()

val db  = FirebaseDatabase.getInstance()

var user : User? = User()

enum class ProgressType {
    NotAsked,
    Loading,
    Result,
    Failure
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(toolbar)

        val prefs = SharedPrefsManager(this)

        if (prefs.getCurrentUser().isNotEmpty()) {
            mainProgressBar.visibility = VISIBLE
            fetchUser(prefs.getCurrentUser())
        }

        signUpButtonMain.setOnClickListener {
            addFragmentToActivity(supportFragmentManager, SignUpView(), R.id.mainActivity)
        }

        mainLoginTV.setOnClickListener {
            addFragmentToActivity(supportFragmentManager, LoginView(), R.id.mainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (this == MainActivity()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    fun fetchUser(savedUID: String) {
        db.reference.child("Users").child(savedUID).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(User::class.java)

                    if (data != null) {
                        user = user?.copy(
                            uid = savedUID,
                            email = data.email,
                            firstName = data.firstName,
                            lastName = data.lastName
                        )
                    }

                    mainProgressBar.visibility = GONE
                    addFragmentToActivity(supportFragmentManager, ReminderView(), R.id.mainActivity)
                }


                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
        )
    }

    fun addFragmentToActivity(manager: FragmentManager?, fragment: Fragment, frameId: Int) {
        val transaction = manager?.beginTransaction()
        transaction?.replace(frameId, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
