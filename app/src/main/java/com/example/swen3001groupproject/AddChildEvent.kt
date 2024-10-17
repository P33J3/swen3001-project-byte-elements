package com.example.swen3001groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddChildEvent : AppCompatActivity() {
    lateinit var add_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_child_event)

        add_button = findViewById(R.id.button2)
        add_button.setOnClickListener {

            //new_event = ChildEvent()

            val intent = Intent(this, ChildEventsActivity::class.java)
            intent.putExtra("new_event", new_event)
            startActivity(intent)
            this.finish()
        }

    }
}