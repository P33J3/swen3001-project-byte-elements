package com.example.swen3001groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChildEventsActivity : AppCompatActivity() {

    val eventList = ArrayList<ChildEvent>()
    lateinit var adapter: EventAdapter
    lateinit var add_event_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        add_event_button = findViewById(R.id.button)
        add_event_button.setOnClickListener {
            val intent = Intent(this, AddChildEvent::class.java)
            startActivity(intent)
        }



        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy event data
        eventList.add(ChildEvent(10, "Midsemester Exam", "January 10, 2024", "3:00PM", "SLT2, UWI Mona", true, false, false, true))
        eventList.add(ChildEvent(13, "Dad's Birthday Party", "January 13, 2024", "5:00PM", "Home", false, true, false, false))
        eventList.add(ChildEvent(23, "Track Meet", "January 23, 2024", "10:00AM", "National Stadium", false, true, false, true))

        val intent2 = intent
        val eventTitle = intent2.getStringExtra("eventTitle")
        val eventDate = intent2.getStringExtra("eventDate")
        val eventTime = intent2.getStringExtra("eventTime")
        val eventLocation = intent2.getStringExtra("eventLocation")
        val indoor = intent2.getStringExtra("indoor").toBoolean()
        val outdoor = intent2.getStringExtra("outdoor").toBoolean()
        val online = intent2.getStringExtra("online").toBoolean()
        eventList.add(ChildEvent(17, eventTitle, eventDate, eventTime, eventLocation, indoor, outdoor, online, true))



        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }



}