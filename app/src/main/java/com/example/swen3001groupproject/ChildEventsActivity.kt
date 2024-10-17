package com.example.swen3001groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.app.DatePickerDialog
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChildEventsActivity : AppCompatActivity() {

    val eventList = ArrayList<ChildEvent>()
    lateinit var adapter: EventAdapter
    lateinit var add_event_button: Button

    lateinit var selectedDate: TextView
    lateinit var btnOpenCalendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = EventAdapter(eventList)

        add_event_button = findViewById(R.id.button)
        add_event_button.setOnClickListener {
            val intent = Intent(this, AddChildEvent::class.java)
            startActivity(intent)
        }



        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        selectedDate = findViewById(R.id.selectedDate)
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar)

        val today = Calendar.getInstance().time
        updateRecyclerViewForDate(today)

        btnOpenCalendar.setOnClickListener {
            showDatePickerDialog()
        }


        // Dummy event data
        eventList.add(ChildEvent(10, "Midsemester Exam", "2024-01-10", "3:00PM", "SLT2, UWI Mona", true, false, false, true))
        eventList.add(ChildEvent(13, "Dad's Birthday Party", "2024-01-13", "5:00PM", "Home", false, true, false, false))
        eventList.add(ChildEvent(23, "Track Meet", "2024-01-23", "10:00AM", "National Stadium", false, true, false, true))

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
        recyclerView.setHasFixedSize(true)
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



}