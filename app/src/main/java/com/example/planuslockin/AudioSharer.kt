package com.example.planuslockin

import android.util.Base64
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.io.FileOutputStream


class AudioSharer {

    fun shareAudio(filePath: String) {
        val base64Audio = encodeAudioToBase64(filePath)
        if (base64Audio != null) {
            storeAudioInFirestore(base64Audio)
        }
    }
        fun encodeAudioToBase64(filePath: String): String? {
            val file = File(filePath)
            val inputStream = FileInputStream(file)
            val bytes = try {
                inputStream.readBytes()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                inputStream.close()
            }

            return bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
        }

        fun storeAudioInFirestore(base64Audio: String) {
            val firestore = FirebaseFirestore.getInstance()
            val userId =
                FirebaseAuth.getInstance().currentUser?.uid ?: return // Make sure user is logged in

            val audioData = mapOf(
                "userId" to userId,
                "audioBase64" to base64Audio,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("audio_files").add(audioData)
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                }
        }

        fun getAudioFromFirestore() {
            val firestore = FirebaseFirestore.getInstance()

            firestore.collection("audio_files").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val audioBase64 = document.getString("audioBase64")
                        val userId = document.getString("userId")

                        // Decode the Base64 string and play the audio
                        if (audioBase64 != null) {
                            decodeBase64ToAudio(audioBase64, "path_to_save_audio.mp3")
                        }
                    }
                }
        }

        fun decodeBase64ToAudio(base64String: String, outputFilePath: String) {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val outputFile = File(outputFilePath)

            try {
                val outputStream = FileOutputStream(outputFile)
                outputStream.write(decodedBytes)
                outputStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}