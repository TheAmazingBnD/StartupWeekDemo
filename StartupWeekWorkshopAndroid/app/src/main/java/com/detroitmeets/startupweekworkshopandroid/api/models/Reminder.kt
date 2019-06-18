package com.detroitmeets.startupweekworkshopandroid.api.models

data class Reminder (
    var id : String? = null,
    var title : String? = "",
    var description : String? = "",
    var isComplete : Boolean? = false,
    var timestamp: Double? = null
)