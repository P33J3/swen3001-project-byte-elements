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


class DeleteEvents : AppCompatActivity() {
    lateinit var btnDeleteEvent: Button
    lateinit var btnCancelDelete: Button

    var documentId: String? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)  // Ensure this layout is created

        db = FirebaseFirestore.getInstance()

        btnDeleteEvent = findViewById(R.id.delete2)
        btnCancelDelete = findViewById(R.id.cancelBtn)

        // Get the documentId passed from the previous activity
        documentId = intent.getStringExtra("documentID")
        Log.d("EditEventsActivity","Document ID : $documentId")

        if (documentId != null) {
            // Display a confirmation or the event details (optional, you can add that part)
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
            finish()  // Close the activity if no event found
        }

        btnDeleteEvent.setOnClickListener {
            val userData = getUserData()

            if (userData != null && documentId != null) {
                val (userId, profileId) = userData

                // Delete the event document from Firestore
                db.collection("users")
                    .document(userId)
                    .collection("profiles")
                    .document(profileId)
                    .collection("events")
                    .document(documentId!!)  // Reference the event document
                    .delete()
                    .addOnSuccessListener {
                        // Handle success, show a toast, and navigate back
                        Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ChildEventsActivity::class.java)
                        startActivity(intent)
                        finish()  // Close the DeleteEventActivity
                    }
                    .addOnFailureListener { e ->
                        // Handle failure, show an error toast
                        Toast.makeText(this, "Error deleting event: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnCancelDelete.setOnClickListener {
            Toast.makeText(this, "Delete Cancelled", Toast.LENGTH_SHORT).show()
            finish()  // Close the DeleteEventActivity
        }
    }

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



