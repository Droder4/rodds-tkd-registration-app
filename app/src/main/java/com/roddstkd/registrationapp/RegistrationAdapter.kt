package com.roddstkd.registrationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegistrationAdapter(
    private val onEditNotes: (Registration) -> Unit,
    private val onMarkEmailSent: (Registration) -> Unit
) : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    private var items: List<Registration> = emptyList()

    fun submitList(newItems: List<Registration>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val tvInfo: TextView = view.findViewById(R.id.tvStudentInfo)
        val tvNotes: TextView = view.findViewById(R.id.tvStudentNotes)
        val btnEditNotes: Button = view.findViewById(R.id.btnEditNotes)
        val btnMarkEmailSent: Button = view.findViewById(R.id.btnMarkEmailSent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registration, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.studentName ?: ""
        holder.tvInfo.text =
            "Club: ${item.location}\nClass: ${item.assignedClass}\nEmail Status: ${item.emailStatus}\nRegistration: ${item.registrationStatus}"
        holder.tvNotes.text = "Notes: ${item.notes ?: ""}"

        holder.btnEditNotes.setOnClickListener { onEditNotes(item) }
        holder.btnMarkEmailSent.setOnClickListener { onMarkEmailSent(item) }
    }

    override fun getItemCount(): Int = items.size
}
