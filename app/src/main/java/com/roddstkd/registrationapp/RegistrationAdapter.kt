package com.roddstkd.registrationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegistrationAdapter : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    private val items = mutableListOf<Registration>()

    fun submitList(newItems: List<Registration>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvEmailStatus: TextView = view.findViewById(R.id.tvEmailStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registration, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvStudentName.text = item.studentName ?: ""
        holder.tvDetails.text =
            "Club: ${item.location}\nClass: ${item.assignedClass}\nAge: ${item.studentAge}\nParent: ${item.parentName}"
        holder.tvEmailStatus.text = "Email: ${item.emailStatus}"
    }

    override fun getItemCount(): Int = items.size
}
