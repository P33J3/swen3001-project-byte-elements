package com.example.planuslockin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddChildEvent : AppCompatActivity() {
    lateinit var add_button: Button
    lateinit var indoorCheckbox: CheckBox
    lateinit var outdoorCheckbox: CheckBox
    lateinit var onlineCheckbox: CheckBox
    lateinit var title: EditText
    lateinit var date: EditText
    lateinit var time: EditText
    lateinit var location: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_child_event)



        indoorCheckbox = findViewById(R.id.addCheckIndoors)
        outdoorCheckbox = findViewById(R.id.addCheckOutdoors)
        onlineCheckbox = findViewById(R.id.addCheckOnline)
        title = findViewById(R.id.NewTitle)
        date = findViewById(R.id.addEventDate)
        time = findViewById(R.id.addEventTime)
        location = findViewById(R.id.addEventLocation)

        val eventTitle = title.text.toString()
        val eventDate = date.text.toString()
        val eventTime = time.text.toString()
        val eventLocation = location.text.toString()
        val indoor = indoorCheckbox.isChecked
        val outdoor = outdoorCheckbox.isChecked
        val online = onlineCheckbox.isChecked


        add_button = findViewById(R.id.button2)
        add_button.setOnClickListener {



            val intent = Intent(this, ChildEventsActivity::class.java)
            intent.putExtra("eventTitle", eventTitle)
            intent.putExtra("eventDate", eventDate)
            intent.putExtra("eventTime", eventTime)
            intent.putExtra("eventLocation", eventLocation)
            intent.putExtra("indoor", indoor)
            intent.putExtra("outdoor", outdoor)
            intent.putExtra("online", online)
            startActivity(intent)
            this.finish()
        }

    }
}