package com.example.planuslockin

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import com.example.planuslockin.databinding.ActivityUserProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ParentEventActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private val eventList = ArrayList<ChildEvent>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_dashboard_layout)

//        // Dummy event data
//        eventList.add(ChildEvent(10, "Midsemester Exam", "2024-01-10", "3:00PM", "SLT2, UWI Mona", true, false, false, true))
//        eventList.add(ChildEvent(13, "Dad's Birthday Party", "2024-01-13", "5:00PM", "Home", false, true, false, false))
//        eventList.add(ChildEvent(23, "Track Meet", "2024-01-23", "10:00AM", "National Stadium", false, true, false, true))

        // Setup CalendarView
        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}-${month + 1}-$dayOfMonth"
            Toast.makeText(this, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventAdapter(eventList)

        // Highlight event dates on the calendar
        highlightEventDates()
    }
    private fun fetchEventsForSelectedDate(selectedDate: String) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            // Fetch events for the selected date from Firestore
            db.collection("users")
                .document(userId)
                .collection("events")
                .get()
                .addOnSuccessListener { result ->
                    eventList.clear()  // Clear the existing events
                    for (document in result) {
                        val event = document.toObject(ChildEvent::class.java)
//                        if (event.date == selectedDate) {
                            eventList.add(event)
//                        }
                    }
                    // Notify the adapter that the data has changed
                    adapter.updateEvents(eventList)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch events", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun highlightEventDates() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val selectedDateString = dateFormat.format(selectedDate.time)

            // Check if the selected date has an event
            val eventOnDate = eventList.any { event ->
                event.date?.let { dateFormat.format(it) } == selectedDateString
            }

            if (eventOnDate) {
                Toast.makeText(this, "Event on $selectedDateString", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
