package com.example.swen3001groupproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChildEventsActivity : AppCompatActivity() {

    val eventList = ArrayList<ChildEvent>()
    lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Dummy event data
//        val eventList = ArrayList<ChildEvent>()

        eventList.add(ChildEvent(10, "Midsemester Exam", "January 10, 2024", "3:00PM", "SLT2, UWI Mona", true, false, false, true))
        eventList.add(ChildEvent(13, "Dad's Birthday Party", "January 13, 2024", "5:00PM", "Home", false, true, false, false))
        eventList.add(ChildEvent(23, "Track Meet", "January 23, 2024", "10:00AM", "National Stadium", false, true, false, true))


        // Set up RecyclerView
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }



}