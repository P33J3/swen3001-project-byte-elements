package com.example.planuslockin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val context: Context, private val profileList: List<Map<String, Any>>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

//    private val profiles = mutableListOf<Map<String, Any>>()
//    lateinit var context: Context



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profiles, parent, false)
        return MyViewHolder(itemView)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: Adapter.MyViewHolder, position: Int) {

        val profile = profileList[position]
        holder.profileName.text = profile["name"].toString()
        holder.profileRole.text = profile["role"].toString()


        holder.itemView.setOnClickListener {
            verifyPIN(profile)

        }

    }

    override fun getItemCount() = profileList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName : TextView = itemView.findViewById(R.id.profile_name)
        val profileRole : TextView = itemView.findViewById(R.id.profile_role)
    }

    private fun verifyPIN(profile: Map<String, Any>) {
        // Create a dialog to ask for the pin
        val pinAlert = AlertDialog.Builder(context)
        pinAlert.setTitle("Enter Pin")

        // Set up the input field
        val input = EditText(context)
        pinAlert.setView(input)

        pinAlert.setPositiveButton("OK") { dialog, _ ->
            val enteredPin = input.text.toString()

            // Check if the entered pin is empty
            if (enteredPin.isEmpty()) {
                // Show a message if the pin is empty
                AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Pin cannot be empty. Please enter a valid pin.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                val storedPin = profile["pin"].toString() // Assume the pin is stored in the profile data

                // Check if the entered pin matches the profile's pin
                if (enteredPin == storedPin) {
                    // If correct, start the appropriate activity
                    if (profile["role"] == "Parent") {
//                    val intent = Intent(context, ParentEventsActivity::class.java)
//                    context.startActivity(intent)
                    } else if (profile["role"] == "Child") {
                        val intent = Intent(context, ChildEventsActivity::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    // If incorrect, show an error message
                    AlertDialog.Builder(context)
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

}