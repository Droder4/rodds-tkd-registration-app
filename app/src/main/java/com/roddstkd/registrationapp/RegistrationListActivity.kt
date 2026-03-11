package com.roddstkd.registrationapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var titleText: TextView
    private lateinit var progressBar: ProgressBar
    private val adapter = RegistrationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_list)

        recyclerView = findViewById(R.id.recyclerView)
        titleText = findViewById(R.id.tvTitle)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val mode = intent.getStringExtra("mode") ?: "all"
        val value = intent.getStringExtra("value") ?: ""

        when (mode) {
            "club" -> {
                titleText.text = "Registrations - $value"
                loadByClub(value)
            }
            "class" -> {
                titleText.text = "Registrations - $value"
                loadByClass(value)
            }
            else -> {
                titleText.text = "All Registrations"
                loadAll()
            }
        }
    }

    private fun loadAll() {
        showLoading(true)
        RetrofitClient.api.getRegistrations().enqueue(registrationCallback())
    }

    private fun loadByClub(club: String) {
        showLoading(true)
        RetrofitClient.api.getByClub(club = club).enqueue(registrationCallback())
    }

    private fun loadByClass(className: String) {
        showLoading(true)
        RetrofitClient.api.getByClass(className = className).enqueue(registrationCallback())
    }

    private fun registrationCallback(): Callback<List<Registration>> {
        return object : Callback<List<Registration>> {
            override fun onResponse(
                call: Call<List<Registration>>,
                response: Response<List<Registration>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val items = response.body().orEmpty()
                    adapter.submitList(items)
                    if (items.isEmpty()) {
                        Toast.makeText(
                            this@RegistrationListActivity,
                            "No registrations found.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegistrationListActivity,
                        "Failed to load registrations.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Registration>>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@RegistrationListActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
