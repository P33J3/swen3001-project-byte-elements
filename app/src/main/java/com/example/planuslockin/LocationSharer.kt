package com.example.planuslockin

import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//class responsible for sharing the location of the user to other users
class LocationSharer {

    fun shareLocation(location: Location) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Log.d("LocationSharer", "User ID is null")
            return
        }

        //Store user location
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId).set(locationData)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->

            }
    }

    fun getOtherUsersLocations(mGoogleMap: GoogleMap?): List<LatLng> {
        val firestore = FirebaseFirestore.getInstance()
        val markersList: MutableList<LatLng> = mutableListOf()

        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    val userId = document.id

                    if (latitude != null && longitude != null) {
                        val userPosition = LatLng(latitude, longitude)
                        mGoogleMap?.addMarker(
                            MarkerOptions().position(userPosition).title("User: $userId")
                        )
                        markersList.add(userPosition)
                    }
                }
            }
        return markersList
    }

}