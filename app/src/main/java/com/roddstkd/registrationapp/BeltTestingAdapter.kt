package com.roddstkd.registrationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BeltTestingAdapter(
    private val onManageClicked: (Registration) -> Unit
) : RecyclerView.Adapter<BeltTestingAdapter.ViewHolder>() {

    private var items: List<Registration> = emptyList()

    fun submitList(newItems: List<Registration>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvBeltStudentName)
        val tvBeltInfo: TextView = view.findViewById(R.id.tvBeltStudentInfo)
        val btnManageBelt: Button = view.findViewById(R.id.btnManageBelt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_belt_testing_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvStudentName.text = item.studentName ?: ""
        holder.tvBeltInfo.text =
            "Class: ${item.assignedClass}\n" +
            "Current Belt: ${item.currentBelt}\n" +
            "Testing For: ${item.testingFor}\n" +
            "Belt Test Date: ${item.beltTestDate}\n" +
            "Invite Status: ${item.beltInviteStatus}"

        holder.btnManageBelt.setOnClickListener {
            onManageClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
