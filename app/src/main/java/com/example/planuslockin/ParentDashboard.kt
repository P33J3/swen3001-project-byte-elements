package com.example.planuslockin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.planuslockin.databinding.ActivityParentDashboardBinding
import com.example.planuslockin.databinding.ActivitySignInBinding

@Suppress("DEPRECATION")
class ParentDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityParentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val calendarFragment = CalendarFragment()
        val settingsFragment = SettingsFragment()

        replaceFragment(HomeFragment())

        binding.parentbottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.parenthome -> replaceFragment(HomeFragment())
                R.id.parentchat -> replaceFragment(ChatFragment())
                R.id.parentcalendar -> replaceFragment(CalendarFragment())
                R.id.parentsettings -> replaceFragment(SettingsFragment())

                else ->{

                }
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.parentframelayout, fragment)
        fragmentTransaction.commit()

    }
}