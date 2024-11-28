package com.example.planuslockin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddChildEvent : AppCompatActivity() {
    lateinit var add_button: Button
    lateinit var indoorCheckbox: CheckBox
    lateinit var onlineCheckbox: CheckBox
    lateinit var title: EditText
    lateinit var date: EditText
    lateinit var time: EditText
    lateinit var location: EditText
    lateinit var shareEvent: CheckBox
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_child_event)

        db = FirebaseFirestore.getInstance()

        indoorCheckbox = findViewById(R.id.addCheckIndoors)
        onlineCheckbox = findViewById(R.id.addCheckOnline)
        title = findViewById(R.id.NewTitle)
        date = findViewById(R.id.addEventDate)
        time = findViewById(R.id.addEventTime)
        location = findViewById(R.id.addEventLocation)
        shareEvent = findViewById(R.id.addKids)

        // Retrieve the selected date passed from the previous activity
        val selectedDateMilliseconds = intent.getLongExtra("SELECTED_DATE", -1L)

        if (selectedDateMilliseconds != -1L) {
            // Convert the milliseconds back to a Date object
            val selectedDate = Date(selectedDateMilliseconds)

            // Format and display the selected date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate)
            date.setText(formattedDate)
        } else {
            // Handle the case where the date wasn't passed correctly
            Toast.makeText(this, "No date received", Toast.LENGTH_SHORT).show()
        }

        date.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, newYear, newMonth, newDay ->
                val changedDate = "$newYear/${newMonth + 1}/$newDay"
                val finalDate = changedDate.replace("/", "-")
                date.setText(finalDate)
            },year,month,day)
            datePickerDialog.show()
        }

        time.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Update the EditText with the selected time
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                time.setText(formattedTime)
            }, hour, minute, true) // 24-hour format (true), false for 12-hour

            timePickerDialog.show()
        }


        add_button = findViewById(R.id.button2)
        add_button.setOnClickListener {

            getUserData()
            val userData = getUserData()
            if (userData != null) {
                val (userId, profileId) = userData

//                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val formattedDate = dateFormat.format(date.text.toString())

                val childEvent = ChildEvent(
                    title = title.text.toString(),
                    date = date.text.toString(),
                    time = time.text.toString(),
                    location = location.text.toString(),
                    isIndoors = indoorCheckbox.isChecked,
                    isOnline = onlineCheckbox.isChecked,
                    shareEvent = shareEvent.isChecked
                )

                db.collection("users")
                    .document(userId)
                    .collection("profiles")
                    .document(profileId)
                    .collection("events")
                    .add(childEvent)
            }





            val intent = Intent(this, ChildEventsActivity::class.java)
//            intent.putExtra("eventTitle", eventTitle)
//            intent.putExtra("eventDate", eventDate)
//            intent.putExtra("eventTime", eventTime)
//            intent.putExtra("eventLocation", eventLocation)
//            intent.putExtra("indoor", indoor)
//            intent.putExtra("online", online)
            startActivity(intent)
            this.finish()
        }

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
}