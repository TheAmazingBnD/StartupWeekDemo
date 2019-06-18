package com.detroitmeets.startupweekworkshopandroid

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.detroitmeets.startupweekworkshopandroid.api.models.Reminder
import com.detroitmeets.startupweekworkshopandroid.api.models.User
import com.detroitmeets.startupweekworkshopandroid.authentication.LoginView
import com.detroitmeets.startupweekworkshopandroid.authentication.LoginViewModel
import com.detroitmeets.startupweekworkshopandroid.authentication.SignUpView
import com.detroitmeets.startupweekworkshopandroid.reminder.ReminderView
import com.detroitmeets.startupweekworkshopandroid.reminder.ReminderViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_main.*

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
            fetchUser(prefs.getCurrentUser())
        }

        signUpButtonMain.setOnClickListener { view ->
            addFragmentToActivity(supportFragmentManager, SignUpView(), R.id.mainActivity)
        }

        mainLoginTV.setOnClickListener {view ->
            addFragmentToActivity(supportFragmentManager, LoginView(), R.id.mainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun fetchUser(savedUID: String) {
        db.reference.child("Users").child(savedUID).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(User::class.java)

                    val email = data?.email
                    val firstName = data?.firstName
                    val lastName = data?.lastName

                    user = user?.copy(
                        uid = savedUID,
                        email = email,
                        firstName = firstName,
                        lastName = lastName
                    )
                    ReminderViewModel(savedUID).fetchReminders(savedUID)

                    addFragmentToActivity(supportFragmentManager, ReminderView(), R.id.mainActivity)
                }


                override fun onCancelled(databaseError: DatabaseError) {
//                                println("The read failed: " + databaseError.code)
                }
            }
        )
    }

    fun setCurrentUser(uid: String) {
        SharedPrefsManager(applicationContext).setCurrentUser(uid)
    }

    fun addFragmentToActivity(manager: FragmentManager?, fragment: Fragment, frameId: Int) {
        val transaction = manager?.beginTransaction()
        transaction?.replace(frameId, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }


}
