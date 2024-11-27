package com.example.planuslockin
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale


class EventAdapter(private val activity: ChildEventsActivity,private var eventList: List<ChildEvent>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // Track which events are expanded (true means expanded, false means collapsed)
    private var expandedState: BooleanArray = BooleanArray(eventList.size)

    init {
        // Initialize expandedState based on the eventList size
        expandedState = BooleanArray(eventList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        if (eventList.isNotEmpty()) {

            val event = eventList[position]

            // Set basic details (day, title, date, time, location)
            val dateFormat =
                SimpleDateFormat("d", Locale.getDefault()) // "d" gives us the day number
            holder.eventDay.text = event.date?.let { dateFormat.format(it) }

            holder.eventTitle.text = event.title

            val fullDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            holder.eventDate.text = event.date?.let { fullDateFormat.format(it) }
            holder.eventTime.text = event.time
            holder.eventLocation.text = event.location

            // Set checkboxes for event type
            holder.checkIndoors.isChecked = event.isIndoors
            holder.checkOnline.isChecked = event.isOnline

            // Set checkboxes for sharing with kids
            holder.checkKids.isChecked = event.shareEvent

            // Handle expanding/collapsing the details layout
            val isExpanded = expandedState[position]
            holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Toggle the expansion state on click
            holder.itemView.setOnClickListener {
                expandedState[position] = !expandedState[position]
                notifyItemChanged(position)  // Notify adapter to refresh this item
            }
            holder.editEventBtn.setOnClickListener{
                val context = holder.itemView.context
                val intent = Intent(context, EditEventsActivity::class.java)
                val documentId=event.id
                Log.d("EventAdapter","passing documentID: ${event.id}")
                intent.putExtra("documentID",documentId) // Pass the event object
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun updateEvents(newEvents: List<ChildEvent>) {
        eventList = newEvents
        expandedState = BooleanArray(eventList.size) // Re-initialize expandedState based on new list size
        notifyDataSetChanged()
    }

    // ViewHolder class that holds references to the views in the item layout
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventDay: TextView = itemView.findViewById(R.id.eventDay)
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)

        val checkIndoors: CheckBox = itemView.findViewById(R.id.cardCheckIndoors)
        val checkOnline: CheckBox = itemView.findViewById(R.id.cardCheckOnline)

        val checkKids: CheckBox = itemView.findViewById(R.id.checkKids)
        val editEventBtn: Button =itemView.findViewById(R.id.editbtn)
        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout)
    }
}
