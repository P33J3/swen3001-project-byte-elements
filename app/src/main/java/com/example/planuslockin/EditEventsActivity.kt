package com.example.planuslockin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore



class EditEventsActivity: AppCompatActivity(){

        lateinit var etEventTitle: EditText
        lateinit var etEventDate: EditText
        lateinit var etEventTime: EditText
        lateinit var etEventLocation: EditText
        lateinit var checkIndoors: CheckBox
        lateinit var checkOutdoors: CheckBox
        lateinit var checkOnline: CheckBox
        lateinit var checkYes: CheckBox
        lateinit var checkNo: CheckBox
        lateinit var btnSaveEvent: Button
        private lateinit var db: FirebaseFirestore

        lateinit var originalEvent: ChildEvent  // To hold the original event for editing

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edit)
            db = FirebaseFirestore.getInstance()

            // Initialize views
            etEventTitle = findViewById(R.id.etEventTitle)
            etEventDate = findViewById(R.id.etEventDate)
            etEventTime = findViewById(R.id.etEventTime)
            etEventLocation = findViewById(R.id.etEventLocation)
            checkIndoors = findViewById(R.id.checkIndoors)
            checkOutdoors = findViewById(R.id.checkOutdoors)
            checkOnline = findViewById(R.id.checkOnline)
            checkYes = findViewById(R.id.checkYes)
            checkNo = findViewById(R.id.checkNo)
            btnSaveEvent = findViewById(R.id.btnSaveEvent)

            // Get the event passed from ChildEventsActivity
            val event = intent.getSerializableExtra("event") as? ChildEvent
            if (event != null) {
                originalEvent = event
                // Pre-fill the fields with the existing event data
                etEventTitle.setText(event.title)
                etEventDate.setText(event.date.toString())
                etEventTime.setText(event.time)
                etEventLocation.setText(event.location)
                checkIndoors.isChecked = event.isIndoors
                checkOnline.isChecked = event.isOnline
                checkOutdoors.isChecked=!checkIndoors.isChecked
                checkYes.isChecked =event.shareEvent
                checkNo.isChecked=!checkYes.isChecked
            }


            // Save button click listener to update the event
            btnSaveEvent.setOnClickListener {
               val updatedEvent = ChildEvent(
                    etEventTitle.text.toString(),
                    etEventDate.text.toString(),
                    etEventTime.text.toString(),
                    etEventLocation.text.toString(),
                    checkIndoors.isChecked,
                    checkOnline.isChecked,
                    checkYes.isChecked
                )

                // Return the updated event to the previous activity
                /*val resultIntent = Intent()
                resultIntent.putExtra("updatedEvent", updatedEvent)
                setResult(RESULT_OK, resultIntent)
                finish()*/  // Finish the activity and return to the previous one
            }
        }
    }


