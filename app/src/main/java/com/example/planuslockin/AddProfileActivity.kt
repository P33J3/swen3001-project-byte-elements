package com.example.planuslockin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planuslockin.databinding.ActivityAddProfileBinding
import com.example.planuslockin.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Handling Spinner
        val spinnerID = findViewById<Spinner>(R.id.spinID)
        val roles = arrayOf("Parent", "Child")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerID.adapter = arrayAdapter
        //Spinner finished

        val userID = firebaseAuth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        binding.addprofilebutton.setOnClickListener {
            val name = binding.addprofilename.text.toString()
            val pin = binding.addprofilepin.text.toString()
            val confirmpin = binding.confirmprofilepin.text.toString()
            val selectedText = spinnerID.selectedItem.toString()

            if (name.isNotEmpty() && pin.isNotEmpty() && confirmpin.isNotEmpty()){
                if (pin == confirmpin){
                    if (selectedText == "Parent" || selectedText == "Child"){

                        val profileData = mapOf(
                            "name" to name,
                            "pin" to pin,
                            "role" to selectedText
                        )

                        db.collection("users")
                            .document(userID)
                            .collection("profiles")
                            .add(profileData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile Added", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, UserProfilesActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add profile" , Toast.LENGTH_SHORT).show()
                                Log.d("MyTag", "The USERID is: " + userID);
                                Log.d("MyTag", "The name is: " + name);
                                Log.d("MyTag", "The pin is: " + pin);
                                Log.d("MyTag", "The role is: " + selectedText);
                            }

                    } else {
                        Toast.makeText(this, "Please Select a Role", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "PIN and Confirmation do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
            }


        }
    }
}