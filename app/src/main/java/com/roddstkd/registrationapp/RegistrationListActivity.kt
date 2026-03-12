package com.roddstkd.registrationapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrationAdapter
    private var allItems: List<Registration> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_list)

        recyclerView = findViewById(R.id.recyclerViewRegistrations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RegistrationAdapter(
            onEditNotes = { item -> showEditNotesDialog(item) },
            onMarkEmailSent = { item -> markWelcomeEmailSent(item) }
        )
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAll).setOnClickListener { adapter.submitList(allItems) }
        findViewById<Button>(R.id.btnCornwall).setOnClickListener {
            adapter.submitList(allItems.filter { it.location == "Cornwall" })
        }
        findViewById<Button>(R.id.btnMontague).setOnClickListener {
            adapter.submitList(allItems.filter { it.location == "Montague" })
        }
        findViewById<Button>(R.id.btnClass1).setOnClickListener {
            adapter.submitList(allItems.filter { it.assignedClass == "Class 1" })
        }
        findViewById<Button>(R.id.btnClass2).setOnClickListener {
            adapter.submitList(allItems.filter { it.assignedClass == "Class 2" })
        }
        findViewById<Button>(R.id.btnClass3).setOnClickListener {
            adapter.submitList(allItems.filter { it.assignedClass == "Class 3" })
        }

        loadRegistrations()
    }

    private fun loadRegistrations() {
        RetrofitClient.api.getRegistrations().enqueue(object : Callback<List<Registration>> {
            override fun onResponse(
                call: Call<List<Registration>>,
                response: Response<List<Registration>>
            ) {
                if (response.isSuccessful) {
                    allItems = response.body().orEmpty()
                    adapter.submitList(allItems)
                } else {
                    Toast.makeText(this@RegistrationListActivity, "Failed to load students.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Registration>>, t: Throwable) {
                Toast.makeText(this@RegistrationListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showEditNotesDialog(item: Registration) {
        val input = EditText(this)
        input.setText(item.notes ?: "")

        AlertDialog.Builder(this)
            .setTitle("Edit Notes")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                updateNotes(item, input.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateNotes(item: Registration, notes: String) {
        val json = JSONObject().apply {
            put("action", "updateStudentNotes")
            put("studentName", item.studentName ?: "")
            put("email", item.email ?: "")
            put("notes", notes)
        }

        val body = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.updateStudentNotes(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@RegistrationListActivity, response.body()?.message ?: "Saved", Toast.LENGTH_LONG).show()
                loadRegistrations()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@RegistrationListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun markWelcomeEmailSent(item: Registration) {
        RetrofitClient.api.markWelcomeEmailSent(
            email = item.email ?: "",
            studentName = item.studentName ?: ""
        ).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@RegistrationListActivity, response.body()?.message ?: "Updated", Toast.LENGTH_LONG).show()
                loadRegistrations()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@RegistrationListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
