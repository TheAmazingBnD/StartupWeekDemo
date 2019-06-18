package com.detroitmeets.startupweekworkshopandroid

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.detroitmeets.startupweekworkshopandroid.SharedPrefsKeys.PREFS_NAME

open class SharedPrefsManager(activity: Context) {
    private var prefs : SharedPreferences =  PreferenceManager.getDefaultSharedPreferences(activity)

    fun getCurrentUser() = prefs.getString(SharedPrefsKeys.USER_UID, "").orEmpty()

    fun setCurrentUser(userId: String) {
        try {
            prefs.edit().putString(SharedPrefsKeys.USER_UID, userId).apply()
        } catch (e: Exception) {
        }
    }
}