package com.example.planusauthenticationregistration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planusauthenticationregistration.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.buttonSignUp.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if(checkAllFields()){
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        auth.signOut()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "Account has been created", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.e("error ", it.exception.toString())
                    }
                }
            }
        }
    }

    private fun checkAllFields(): Boolean{
        val email = binding.etEmail.text.toString()
        if(binding.etEmail.text.toString() == ""){
            binding.textInputLayoutEmail.error = "Please fill in Email field"
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textInputLayoutEmail.error = "Please enter a correct email format"
            return false
        }

        if(binding.etPassword.text.toString() == ""){
            binding.textInputLayoutPassword.error = "Please enter a password"
            binding.textInputLayoutPassword.errorIconDrawable = null
            return false
        }
        //password must be greater than 4 character long
        if(binding.etPassword.length() <= 5){
            binding.textInputLayoutPassword.error = "Please enter a longer password, passwords should be at least 5 characters long"
            binding.textInputLayoutPassword.errorIconDrawable = null
            return false
        }

        if(binding.etConfirmPassword.text.toString() == ""){
            binding.textInputLayoutConfirmPassword.error = "Please enter password confirmation"
            binding.textInputLayoutConfirmPassword.errorIconDrawable = null
            return false
        }

        if(binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()){
            binding.textInputLayoutPassword.error = "Passwords do not match"
            return false
        }
        return true
    }
}