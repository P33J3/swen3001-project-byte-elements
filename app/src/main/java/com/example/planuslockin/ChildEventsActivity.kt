package com.example.planuslockin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChildEventsActivity : AppCompatActivity() {

    val eventList = ArrayList<ChildEvent>()
//    val fetchedEvents = ArrayList<ChildEvent>()
    lateinit var adapter: EventAdapter
    lateinit var add_event_button: Button

    lateinit var selectedDate: TextView
    lateinit var btnOpenCalendar: Button

    val calendar = Calendar.getInstance()
    val currentSelection = calendar.time

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_events)
        adapter = EventAdapter(eventList)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        add_event_button = findViewById(R.id.button)
        add_event_button.setOnClickListener {
            val intent = Intent(this, AddChildEvent::class.java)
            intent.putExtra("SELECTED_DATE", currentSelection.time)
            startActivity(intent)
        }



        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        selectedDate = findViewById(R.id.selectedDate)
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar)

        val today = Calendar.getInstance().time
        updateRecyclerViewForDate(today)

        btnOpenCalendar.setOnClickListener {
            showDatePickerDialog()
        }




        // Dummy event data
//        eventList.add(ChildEvent( "Midsemester Exam", "2024-01-10", "3:00PM", "SLT2, UWI Mona", true, false, false))
//        eventList.add(ChildEvent( "Dad's Birthday Party", "2024-01-13", "5:00PM", "Home", false, true, false))
//        eventList.add(ChildEvent( "Track Meet", "2024-01-23", "10:00AM", "National Stadium", false, true, false))

        //val intent2 = intent
        //val eventTitle = intent2.getStringExtra("eventTitle")
        //val eventDate = intent2.getStringExtra("eventDate")
        //val eventTime = intent2.getStringExtra("eventTime")
        //val eventLocation = intent2.getStringExtra("eventLocation")
        //val indoor = intent2.getStringExtra("indoor").toBoolean()
        //val outdoor = intent2.getStringExtra("outdoor").toBoolean()
        //val online = intent2.getStringExtra("online").toBoolean()
        //eventList.add(ChildEvent(17, eventTitle, eventDate, eventTime, eventLocation, indoor, outdoor, online, true))



        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        fetchCalendarEvents()

        // Now update the eventList in the adapter
//        eventList.clear()  // Clear the existing events
//        eventList.addAll(fetchedEvents)  // Add the fetched events


        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged()
        Log.d("FirestoreData", "Events List: ${eventList.size}")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Open the DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Update the TextView with the selected date
            calendar.set(selectedYear, selectedMonth, selectedDay)
            val datePickerSelectedDate = calendar.time
            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            selectedDate.text = "Selected Date: ${dateFormat.format(datePickerSelectedDate)}"
            currentSelection.time = datePickerSelectedDate.time

            // Update RecyclerView with events closest to the selected date
            updateRecyclerViewForDate(datePickerSelectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    // Method to filter events based on the selected date and update RecyclerView
    private fun updateRecyclerViewForDate(selectedDate: Date) {
        val filteredEvents = getClosestEvents(selectedDate, 5)
        adapter.updateEvents(filteredEvents)
    }

    // Helper function to get the closest events to the selected date
    private fun getClosestEvents(selectedDate: Date, count: Int): List<ChildEvent> {
        return eventList
            .sortedBy { event ->
                val eventDate = event.date // Assuming event.date is of type Date
                Math.abs(eventDate?.time!! - selectedDate.time) // Returns a Long, which is Comparable
            }
            .take(count) // Get the closest `count` number of events
    }

    private fun getUserData(): Pair<String, String>? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val profileId = sharedPreferences.getString("profile_id", null)

        return if (userId != null && profileId != null) {
            Pair(userId, profileId)
        } else {
            null // or handle the case where data is not available
        }
    }

    private fun fetchCalendarEvents() {

//        val fireUserId = firebaseAuth.currentUser
//        if (fireUserId == null) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
//            return
//        }

        val userData = getUserData()
        if (userData != null) {
            val (userId, profileId) = userData
            // Now use the userId and profileId to query Firebase or make network requests
            db.collection("users")
                .document(userId)
                .collection("profiles")
                .document(profileId)
                .collection("events")
                .get()
                .addOnSuccessListener { result ->
                    // Process the events and update the RecyclerView
                    val fetchedEvents = ArrayList<ChildEvent>()

                    // Iterate through the results and map them to ChildEvent objects
                    for (document in result) {

                        // Log the raw document data to see what we're getting from Firestore
                        Log.d("FirestoreData", "Document ID: ${document.id}")
                        Log.d("FirestoreData", "Document Data: ${document.data}")

//                        val event = document.toObject(ChildEvent::class.java)
                        val event = ChildEvent().fromFirestore(document)
                        fetchedEvents.add(event)
                    }

                    // Log the list of events fetched
                    Log.d("FirestoreData", "Fetched Events: ${fetchedEvents.joinToString()}")

//                    // Now update the eventList in the adapter
                    eventList.clear()  // Clear the existing events
                    eventList.addAll(fetchedEvents)  // Add the fetched events
                    adapter.updateEvents(fetchedEvents)
//
//                    // Notify the adapter that the data has changed
//                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch events", Toast.LENGTH_LONG).show()
                    Log.e("ChildEventsActivity", "Error fetching events: ")
                }
        } else {
            // Handle the case where user data is not available
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
        }
    }



}