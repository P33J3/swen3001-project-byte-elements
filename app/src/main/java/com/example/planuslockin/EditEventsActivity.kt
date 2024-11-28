package com.example.planuslockin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth




class EditEventsActivity : AppCompatActivity() {

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
    private var documentId: String? = null
    private lateinit var db: FirebaseFirestore

    //lateinit var originalEvent: ChildEvent  // To hold the original event for editing

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
       // val event = intent.getSerializableExtra("event") as? ChildEvent
        /*if (event != null) {
            originalEvent = event
            // Pre-fill the fields with the existing event data
            etEventTitle.setText(event.title)
            etEventDate.setText(event.date.toString())
            etEventTime.setText(event.time)
            etEventLocation.setText(event.location)
            checkIndoors.isChecked = event.isIndoors
            checkOnline.isChecked = event.isOnline
            checkOutdoors.isChecked = !checkIndoors.isChecked
            checkYes.isChecked = event.shareEvent
            checkNo.isChecked = !checkYes.isChecked
        }*/
        documentId = intent.getStringExtra("documentID")
        Log.d("EditEventsActivity","Document ID : $documentId")

        if (documentId != null) {
            db.collection("users")
                .document(getUserData()?.first ?: return) // Assuming the userId is passed from shared preferences
                .collection("profiles")
                .document(getUserData()?.second ?: return) // Assuming profileId is passed from shared preferences
                .collection("events")
                .document(documentId!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        //val event = document.toObject(ChildEvent::class.java)
                        val event= ChildEvent().fromFirestore(document)

                        if (event != null) {
                            // Pre-fill the fields with the existing event data
                            etEventTitle.setText(event.title)
                            etEventDate.setText(event.date.toString())
                            etEventTime.setText(event.time)
                            etEventLocation.setText(event.location)
                            checkIndoors.isChecked = event.isIndoors
                            checkOnline.isChecked = event.isOnline
                            checkOutdoors.isChecked = !checkIndoors.isChecked
                            checkYes.isChecked = event.shareEvent
                            checkNo.isChecked = !checkYes.isChecked
                        }
                    }else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch event: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        // Save button click listener to update the event
        btnSaveEvent.setOnClickListener {
            val userData = getUserData()
            if (userData != null && documentId != null) {
                val (userId, profileId) = userData

                val updatedEvent = ChildEvent(
                    documentId,  // Keep the document ID to update the correct event
                    etEventTitle.text.toString(),
                    etEventDate.text.toString(),
                    etEventTime.text.toString(),
                    etEventLocation.text.toString(),
                    checkIndoors.isChecked,
                    checkOnline.isChecked,
                    checkYes.isChecked
                )

                // Update the event in Firestore
                db.collection("users")
                    .document(userId)
                    .collection("profiles")
                    .document(profileId)
                    .collection("events")
                    .document(documentId!!) // Use the document ID to update the correct event
                    .set(updatedEvent)
                    .addOnSuccessListener {
                        // Handle success, show a toast, and navigate back
                        Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ChildEventsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure, show an error toast
                        Toast.makeText(this, "Error updating event: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Get user and profile ID from SharedPreferences
    private fun getUserData(): Pair<String, String>? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val profileId = sharedPreferences.getString("profile_id", null)

        return if (userId != null && profileId != null) {
            Pair(userId, profileId)
        } else {
            null // Return null if user or profile ID is not found
        }
    }
}
