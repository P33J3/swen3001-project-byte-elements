package com.example.planuslockin

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChildEvent: Serializable {
    var id: String? = null
    var title: String? = null
    var date: Date? = null  //yyyy-MM-dd
    var time: String? = null
    var location: String? = null
    var isIndoors = false
    var isOnline = false
    var shareEvent = false

    constructor(
        id:String?, title: String?, date: String, time: String?, location: String?,
        isIndoors: Boolean, isOnline: Boolean, shareEvent: Boolean
    ) {
        this.id= id
        this.title = title
        this.date = parseDate(date) ?: Date() // Use default date if invalid
        this.time = time
        this.location = location
        this.isIndoors = isIndoors
        this.isOnline = isOnline
        this.shareEvent = shareEvent
    }

    constructor() : this(null,null, "", null, null, false, false, false)

    // Add a custom toString() method to log the content properly
    override fun toString(): String {
        return "ChildEvent(title='$title', time='$time', location='$location', " +
                "date=$date, indoors=$isIndoors, online=$isOnline, shareEvent=$shareEvent)"
    }

    companion object {
        // Function to parse a date string into a Date object
        fun parseDate(dateString: String): Date? {
            if (dateString.isNullOrEmpty()) {
                // Handle empty or null date strings gracefully
                return null
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null  // Return null instead of throwing exception
            }
        }

        // Function to convert Firestore Timestamp to a Date string
        fun formatDateToString(date: Date): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(date)
        }
    }

    // Function to convert Firestore Timestamp to Date
    fun fromFirestore(document: DocumentSnapshot): ChildEvent {
        val id = document.id //generated id from firebase
        val title = document.getString("title")
        val time = document.getString("time")
        val location = document.getString("location")
        val isIndoors = document.getBoolean("indoors") ?: false
        val isOnline = document.getBoolean("online") ?: false
        val shareEvent = document.getBoolean("shareEvent") ?: false
        val dateTimestamp = document.getTimestamp("date")  // Firestore Timestamp

        // Handle missing or null dateTimestamp
        val dateString = if (dateTimestamp != null) {
            formatDateToString(dateTimestamp.toDate())  // Convert Timestamp to string
        } else {
            ""  // Return an empty string or use a default value if missing
        }

        return ChildEvent(id,title, dateString, time, location, isIndoors, isOnline, shareEvent)
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChildEvent) return false
        return title == other.title && date == other.date
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (date?.hashCode() ?: 0)
        return result
    }
}