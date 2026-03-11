package com.roddstkd.registrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnViewRegistrations = findViewById<Button>(R.id.btnViewRegistrations)
        val btnViewByClub = findViewById<Button>(R.id.btnViewByClub)
        val btnViewByClass = findViewById<Button>(R.id.btnViewByClass)
        val btnSendWelcomeEmails = findViewById<Button>(R.id.btnSendWelcomeEmails)

        btnViewRegistrations.setOnClickListener {
            openListScreen("all", "")
        }

        btnViewByClub.setOnClickListener {
            openListScreen("club", "Cornwall")
        }

        btnViewByClass.setOnClickListener {
            openListScreen("class", "Class 1")
        }

        btnSendWelcomeEmails.setOnClickListener {
            sendPendingEmails()
        }
    }

    private fun openListScreen(mode: String, value: String) {
        val intent = Intent(this, RegistrationListActivity::class.java).apply {
            putExtra("mode", mode)
            putExtra("value", value)
        }
        startActivity(intent)
    }

    private fun sendPendingEmails() {
        RetrofitClient.api.sendPendingEmails().enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(
                call: Call<SendEmailResponse>,
                response: Response<SendEmailResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Toast.makeText(
                        this@MainActivity,
                        body.message,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to send emails.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
