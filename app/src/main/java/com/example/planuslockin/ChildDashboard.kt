package com.example.planuslockin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planuslockin.databinding.ActivityChildDashboardBinding

@Suppress("DEPRECATION")
class ChildDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityChildDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val calendarFragment = CalendarFragment()
        val settingsFragment = SettingsFragment()

        replaceFragment(HomeFragment())

        binding.childbottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.childhome -> replaceFragment(HomeFragment())
                R.id.childchat -> replaceFragment(ChatFragment())
                R.id.childcalendar -> replaceFragment(CalendarFragment())
                R.id.childsettings -> replaceFragment(SettingsFragment())

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