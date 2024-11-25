package com.example.planuslockin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val profileList: List<Map<String, Any>>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profiles, parent, false)
        return MyViewHolder(itemView)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: Adapter.MyViewHolder, position: Int) {

        val profile = profileList[position]
        holder.profileName.text = profile["name"].toString()
        holder.profileRole.text = profile["role"].toString()

    }

    override fun getItemCount() = profileList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName : TextView = itemView.findViewById(R.id.profile_name)
        val profileRole : TextView = itemView.findViewById(R.id.profile_role)
    }
}