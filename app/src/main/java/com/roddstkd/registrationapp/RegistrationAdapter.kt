package com.roddstkd.registrationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegistrationAdapter(
    private val onManageClicked: (Registration) -> Unit
) : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    private var items: List<Registration> = emptyList()

    fun submitList(newItems: List<Registration>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val tvInfo: TextView = view.findViewById(R.id.tvStudentInfo)
        val tvStatus: TextView = view.findViewById(R.id.tvStudentStatus)
        val btnManage: Button = view.findViewById(R.id.btnManageStudent)
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
            "Club: ${item.location}\nClass: ${item.assignedClass}\nEmail: ${item.email}"

        holder.tvStatus.text =
            "Payment: ${item.paymentStatus}\n" +
            "Amount Paid: ${item.amountPaid}\n" +
            "Current Belt: ${item.currentBelt}\n" +
            "Testing For: ${item.testingFor}\n" +
            "Welcome Email: ${item.emailStatus}\n" +
            "Belt Invite: ${item.beltInviteStatus}"

        holder.btnManage.setOnClickListener {
            onManageClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
