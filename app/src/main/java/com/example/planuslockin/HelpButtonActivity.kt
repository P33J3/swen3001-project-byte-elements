package com.example.planuslockin

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileInputStream
import java.net.Socket

class HelpButtonActivity : AppCompatActivity(), OnMapReadyCallback {
    private val MICROPHONE_PERMISSION_CODE = 200
    private val FINE_PERMISSION_CODE = 1

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "You" //if user is not logged in, use "You"

    private var mGoogleMap:GoogleMap? = null
    private lateinit var user_location: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mediarecorder: MediaRecorder
    private var isRecording = false
    private lateinit var mediaplayer: MediaPlayer

    private var markersList: List<LatLng> = mutableListOf()
    private var currentMarkerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_activity)

        val forward_button = findViewById<Button>(R.id.right_button)
        forward_button.setOnClickListener {
            cycle_through_markers("forward")
        }
        val backward_button = findViewById<Button>(R.id.left_button)
        backward_button.setOnClickListener {
            cycle_through_markers("backward")
        }
        val exit_button = findViewById<Button>(R.id.exit_button)
        exit_button.setOnClickListener {
            exit_clicked()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(is_mic_present()){
            get_mic_permissions()
        }
        Toast.makeText(this, "Microphone is present", Toast.LENGTH_SHORT).show()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        get_last_location()
        if (::user_location.isInitialized) {
            updateMap(user_location, userId)
            LocationSharer().shareLocation(user_location)
            markersList = LocationSharer().getOtherUsersLocations(mGoogleMap)
        }
    }

    private fun updateMap(location: Location, title: String) {
        val userPos = LatLng(location.latitude, location.longitude)
        mGoogleMap?.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(userPos, 15f))
            addMarker(MarkerOptions().position(userPos).title(title))
        }
    }

    //get the last location of the user and places a marker for it on the map
    private fun get_last_location() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                user_location = location
                updateMap(location, userId)
            } else {
                requestNewLocationData()
            }
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 // 1 second
            fastestInterval = 500
            numUpdates = 1
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        user_location = location
                        updateMap(location, userId)
                        Toast.makeText(this@HelpButtonActivity, "Location marker placed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun cycle_through_markers(direction: String) {
        if (markersList.isEmpty()) {
            Toast.makeText(this, "No markers to cycle through", Toast.LENGTH_SHORT).show()
            return
        }

        if (direction == "forward") {
            currentMarkerIndex = (currentMarkerIndex + 1) % markersList.size
        } else if (direction == "backward") {
            currentMarkerIndex = if (currentMarkerIndex - 1 < 0) {
                markersList.size - 1
            } else {
                currentMarkerIndex - 1
            }
        }
        val nextMarker = markersList[currentMarkerIndex]
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(nextMarker, 15f))
    }

    //if permissions are granted, get the last location
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                get_last_location()
            } else {
                Toast.makeText(
                    this, "Location permission is denied, please allow the permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exit_clicked() {
        finish()
    }


    //if the mic button is pressed, start or stop recording
    fun mic_pressed() {
        try {
            if (isRecording) {
                mediarecorder.stop()
                mediarecorder.release()
                isRecording = false
                send_audio(File(get_recording_file_path()), "10.0.2.2")
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
            }else{
                mediarecorder = MediaRecorder()

                mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediarecorder.setOutputFile(get_recording_file_path())
                mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    mediarecorder.prepare()
                    mediarecorder.start()
                    isRecording = true
                    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            if (!is_microphone_available()) {
                Toast.makeText(this, "Microphone is unavailable", Toast.LENGTH_SHORT).show()
                return
            }
            Toast.makeText(this, "Error recording: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun send_audio(file: File, serverIp: String) {
        //to be added
    }

    fun play_audio() {
        try {
            mediaplayer = MediaPlayer()
            mediaplayer.setDataSource(get_recording_file_path())
            mediaplayer.prepare()
            mediaplayer.start()
            Toast.makeText(this, "Playing audio", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun is_mic_present(): Boolean {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true
        } else {
            return false
        }
    }

    private fun is_microphone_available(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return !audioManager.isMicrophoneMute
    }


    private fun get_mic_permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MICROPHONE_PERMISSION_CODE
            )
        } else {
            Toast.makeText(this, "Microphone permission already granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun get_recording_file_path(): String {
        val contextWrapper = ContextWrapper(applicationContext)
        val musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(musicDirectory, "testRecordingFile" + ".mp3")
        return file.path

    }


}