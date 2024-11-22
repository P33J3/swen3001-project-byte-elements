package com.example.swen3001groupproject

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChildEvent {
    var day = 0
    var title: String? = null
    var date: Date? = null  //yyyy-MM-dd
    var time: String? = null
    var location: String? = null
    var isIndoors = false
    var isOutdoors = false
    var isOnline = false
    var shareWithKids = false

    constructor(
        day: Int, title: String?, date: String, time: String?, location: String?,
    isIndoors: Boolean, isOutdoors: Boolean, isOnline: Boolean, shareWithKids: Boolean
    ) {
        this.day = day
        this.title = title
        this.date = parseDate(date) ?: Date()
        this.time = time
        this.location = location
        this.isIndoors = isIndoors
        this.isOutdoors = isOutdoors
        this.isOnline = isOnline
        this.shareWithKids = shareWithKids
    }

    companion object {
        // Function to parse a date string into a Date object
        fun parseDate(dateString: String): Date? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                dateFormat.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
                throw IllegalArgumentException("Invalid date format: $dateString")
            }
        }
    }
}