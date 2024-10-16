package com.example.swen3001groupproject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val eventList: List<ChildEvent>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // Track which events are expanded (true means expanded, false means collapsed)
    private val expandedState: BooleanArray = BooleanArray(eventList.size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        // Set basic details (day, title, date, time, location)
        holder.tvEventDay.text = event.day.toString()
        holder.tvEventTitle.text = event.title
        holder.tvDate.text = "Date: ${event.date}"
        holder.tvTime.text = "Time: ${event.time}"
        holder.tvLocation.text = "Location: ${event.location}"

        // Set checkboxes for event type
        holder.checkIndoors.isChecked = event.isIndoors
        holder.checkOutdoors.isChecked = event.isOutdoors
        holder.checkOnline.isChecked = event.isOnline

        // Set checkboxes for sharing with kids
        holder.checkYes.isChecked = event.shareWithKids
        holder.checkNo.isChecked = !event.shareWithKids

        // Handle expanding/collapsing the details layout
        val isExpanded = expandedState[position]
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Toggle the expansion state on click
        holder.itemView.setOnClickListener {
            expandedState[position] = !expandedState[position]
            notifyItemChanged(position)  // Notify adapter to refresh this item
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    // ViewHolder class that holds references to the views in the item layout
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEventDay: TextView = itemView.findViewById(R.id.tvEventDay)
        val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        val checkIndoors: CheckBox = itemView.findViewById(R.id.checkIndoors)
        val checkOutdoors: CheckBox = itemView.findViewById(R.id.checkOutdoors)
        val checkOnline: CheckBox = itemView.findViewById(R.id.checkOnline)

        val checkYes: CheckBox = itemView.findViewById(R.id.checkYes)
        val checkNo: CheckBox = itemView.findViewById(R.id.checkNo)

        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout)
    }
}
