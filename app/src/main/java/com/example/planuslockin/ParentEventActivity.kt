package com.example.planuslockin

import EventDecorator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

import com.example.planuslockin.databinding.ActivityUserProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ParentEventActivity : AppCompatActivity() {
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var recyclerView: RecyclerView
    private val eventList = ArrayList<ChildEvent>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_dashboard_layout)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        adapter = EventAdapter(ChildEventsActivity(), eventList)
//         Dummy event data
//        eventList.add(ChildEvent( "Midsemester Exam", "2024-01-10", "3:00PM", "SLT2, UWI Mona", true, false, false))
//        eventList.add(ChildEvent( "Dad's Birthday Party", "2024-01-13", "5:00PM", "Home", false, true, false))
//        eventList.add(ChildEvent( "Track Meet", "2024-01-23", "10:00AM", "National Stadium", false, true, false))

        // Setup CalendarView
        calendarView = findViewById(R.id.calendarView)
//        calendarView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//        calendarView.setDateTextAppearance(R.style.CustomCalendarDateTextStyle)
//        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val selectedDate = "${year}-${month + 1}-$dayOfMonth"
//            Toast.makeText(this, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
//        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchCalendarEvents()
        highlightEventDates(eventList)
        adapter.notifyDataSetChanged()
        Log.d("FirestoreData", "Events List: ${eventList.size}")


    }
    private fun fetchCalendarEvents() {
        val userData = getUserData()  // Get current user data
        if (userData != null) {
            val (userId, profileId) = userData
            Log.d("FirestoreData", "User ID: $userId, ProfileID: $profileId")

            // Fetch all profiles for the current user
            db.collection("users")
                .document(userId)
                .collection("profiles")
                .get()
                .addOnSuccessListener { profileSnapshot ->
                    val fetchedEventsSet = LinkedHashSet<ChildEvent>()  // Use a LinkedHashSet to maintain order and remove duplicates

                    // Loop through all profiles for the current user
                    for (profileDoc in profileSnapshot) {
                        val profileId = profileDoc.id
                        Log.d("FirestoreData", "Fetching events for Profile ID: $profileId")

                        // Get events for each profile
                        db.collection("users")
                            .document(userId)
                            .collection("profiles")
                            .document(profileId)
                            .collection("events")
                            .get()
                            .addOnSuccessListener { eventSnapshot ->
                                // Loop through events for this profile
                                for (eventDoc in eventSnapshot) {
                                    val event = ChildEvent().fromFirestore(eventDoc)
                                    fetchedEventsSet.add(event)
                                }

                                // Log the events fetched for this profile
                                Log.d("FirestoreData", "Fetched Events for $profileId: ${fetchedEventsSet.joinToString()}")

                                // Once all events are fetched, update the eventList in the adapter
                                val fetchedEventsList = ArrayList(fetchedEventsSet)
                                eventList.clear()
                                eventList.addAll(fetchedEventsList)
                                adapter.updateEvents(fetchedEventsList)

                                // Highlight event dates on the calendar
                                highlightEventDates(fetchedEventsList)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirestoreData", "Error fetching events for profile $profileId", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreData", "Error fetching profiles for user $userId", exception)
                    Toast.makeText(this, "Failed to fetch profiles", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
        }
    }


    private fun highlightEventDates(events: List<ChildEvent>) {
        // Log the list of events being processed
        Log.d("HighlightEvents", "Highlighting events: ${events.joinToString { it.title ?: "Unnamed Event" }}")

        // Convert ChildEvent's date to CalendarDay instances
        val eventDates = events.mapNotNull { event ->
            // Log the original Date object
            Log.d("HighlightEvents", "Original event date: ${event.date}")

            event.date?.let {
                // Create a CalendarDay from the Date object
                val calendar = Calendar.getInstance()
                calendar.time = it  // Set the Calendar time to the event's date

                val calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))

                // Log the CalendarDay conversion
                Log.d("HighlightEvents", "Converted CalendarDay: $calendarDay")

                calendarDay  // Return the CalendarDay object
            }
        }

        // Log the total number of event dates to highlight
        Log.d("HighlightEvents", "Total event dates to highlight: ${eventDates.size}")

        // Create an EventDecorator to highlight these dates
        val eventDecorator = EventDecorator(eventDates, this)

        // Add the event decorator to the calendar view to highlight the event dates
        calendarView.addDecorator(eventDecorator)

        // Log that the decorator has been applied
        Log.d("HighlightEvents", "Event decorator applied to calendar view")
    }

    private fun getUserData(): Pair<String, String>? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val profileId = sharedPreferences.getString("profile_id", null)

        return if (userId != null && profileId != null) {
            Pair(userId, profileId)
        } else {
            null
        }
    }





}