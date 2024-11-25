package com.example.planuslockin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planuslockin.databinding.ActivitySignUpBinding
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
                    profiles.add(profile)
                }
                displayProfiles()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch profiles", Toast.LENGTH_SHORT).show()
            }

    }

    private fun displayProfiles() {
        val adapter = Adapter(this, profiles)
        recylcerView.adapter = adapter
    }
}