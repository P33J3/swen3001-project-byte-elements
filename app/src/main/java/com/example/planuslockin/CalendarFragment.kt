package com.example.planuslockin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planuslockin.databinding.FragmentCalendarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private val eventList = ArrayList<ChildEvent>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        setupViews()
        highlightEventDates()
        return binding.root
    }

    private fun setupViews() {
        calendarView = binding.calendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}-${month + 1}-$dayOfMonth"
            Toast.makeText(requireContext(), "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
            fetchEventsForSelectedDate(selectedDate)
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventAdapter(ChildEventsActivity(), eventList)
        recyclerView.adapter = adapter
    }

    private fun fetchEventsForSelectedDate(selectedDate: String) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("events")
                .get()
                .addOnSuccessListener { result ->
                    eventList.clear()
                    for (document in result) {
                        val event = document.toObject(ChildEvent::class.java)
                        eventList.add(event)
                    }
                    adapter.updateEvents(eventList)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightEventDates() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val selectedDateString = dateFormat.format(selectedDate.time)

            val eventOnDate = eventList.any { event ->
                event.date?.let { dateFormat.format(it) } == selectedDateString
            }

            if (eventOnDate) {
                Toast.makeText(requireContext(), "Event on $selectedDateString", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}