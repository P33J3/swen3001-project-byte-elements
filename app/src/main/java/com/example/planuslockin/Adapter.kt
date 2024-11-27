package com.example.planuslockin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val context: Context,
              private var profileList: List<Map<String, Any>>,
              private val onProfileClickListener: UserProfilesActivity,
              private val userId: String
) : RecyclerView.Adapter<Adapter.MyViewHolder>() {


    interface OnProfileClickListener {
        fun onProfileClick(userId:String, profileId: String, pin: String, role: String)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profiles, parent, false)
        return MyViewHolder(itemView)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: Adapter.MyViewHolder, position: Int) {

        val profile = profileList[position]
        holder.profileName.text = profile["name"]?.toString() ?: "No Name"
        holder.profileRole.text = profile["role"]?.toString() ?: "No Role"

        holder.itemView.setOnClickListener {
            val profileId = profile["id"].toString()
            val pin = profile["pin"].toString()
            val role = profile["role"].toString()
            val userId = userId
            onProfileClickListener.onProfileClick(userId,profileId, pin, role)

        }

    }

    override fun getItemCount() = profileList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName : TextView = itemView.findViewById(R.id.profile_name)
        val profileRole : TextView = itemView.findViewById(R.id.profile_role)
    }

    fun updateProfiles(newProfiles: List<Map<String, Any>>) {
        this.profileList = newProfiles
        notifyDataSetChanged()

    }



}