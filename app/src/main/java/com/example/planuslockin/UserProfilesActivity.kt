package com.example.planuslockin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planuslockin.databinding.ActivityUserProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfilesActivity : AppCompatActivity() {

    private lateinit var recylcerView: RecyclerView
    private lateinit var addProfileButton: Button
    private val profiles = mutableListOf<Map<String, Any>>()

    private lateinit var binding: ActivityUserProfilesBinding

    private lateinit var adapter : Adapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recylcerView = findViewById(R.id.recyclerView)
        addProfileButton = findViewById(R.id.addprofileredirect)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recylcerView.layoutManager = layoutManager

        fetchProfiles()

        addProfileButton.setOnClickListener {
            val intent = Intent(this, AddProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchProfiles() {

        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .document(userId)
            .collection("profiles")
            .get()
            .addOnSuccessListener { result ->
                profiles.clear()
                for (document in result) {
                    val profile = document.data
                    profile["id"] = document.id
                    profiles.add(profile)
                }
                displayProfiles()
                // Log the list of profiles fetched
//                Log.d("FirestoreData", "Fetched Profiles: ${profiles.joinToString()}")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch profiles", Toast.LENGTH_SHORT).show()
            }

    }

    private fun displayProfiles() {
        val userId = firebaseAuth.currentUser?.uid
        // Log the profiles list before passing to the adapter
//        Log.d("UserProfiles", "Profiles to display: $profiles")
        val adapter = userId?.let { Adapter(this, profiles, this, it) }
        recylcerView.adapter = adapter
        adapter?.updateProfiles(profiles)
    }

    fun onProfileClick(userId : String, profileId: String, pin:  String, role: String) {
        verifyPIN(userId, profileId, pin, role)
    }

    private fun verifyPIN( userId: String, profileId: String, pin: String, role: String) {
        // Create a dialog to ask for the pin
        val pinAlert = AlertDialog.Builder(this)
        // Set up the input field
        val pinEditText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "Enter PIN"
            maxLines = 1
        }

        pinAlert.setTitle("Enter Pin")
        pinAlert.setView(pinEditText)




        pinAlert.setPositiveButton("OK") { dialog, _ ->
            val enteredPin = pinEditText.text.toString()
//            Log.d("FirestoreData", "VerifyPIN Function - $profileId")

            // Check if the entered pin is empty
            if (enteredPin.isEmpty()) {
                // Show a message if the pin is empty
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Pin cannot be empty. Please enter a valid pin.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {

                // Check if the entered pin matches the profile's pin
                if (enteredPin == pin) {
                    saveUserData(userId, profileId)
                    // If correct, start the appropriate activity
                    if (role == "Parent") {
                        val intent = Intent(this, ParentEventActivity::class.java)
                        this.startActivity(intent)
                    } else if (role == "Child") {
                        val intent = Intent(this, ChildEventsActivity::class.java)
                        this.startActivity(intent)
                    }
                } else {
                    // If incorrect, show an error message
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Incorrect pin entered. Please try again.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }

            dialog.dismiss()
        }

        pinAlert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel() // Close the dialog without doing anything
        }

        pinAlert.show()
    }

    private fun saveUserData(userId: String, profileId: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putString("user_id", userId)
        editor.putString("profile_id", profileId)
        editor.apply()
    }
}