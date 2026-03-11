package com.roddstkd.registrationapp

import android.app.AlertDialog
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
        val btnSendCompleteEmails = findViewById<Button>(R.id.btnSendCompleteEmails)
        val btnSendIncompleteReminders = findViewById<Button>(R.id.btnSendIncompleteReminders)

        btnViewRegistrations.setOnClickListener {
            openListScreen("all", "")
        }

        btnViewByClub.setOnClickListener {
            showClubChooser()
        }

        btnViewByClass.setOnClickListener {
            showClassChooser()
        }

        btnSendWelcomeEmails.setOnClickListener {
            sendWelcomeEmails()
        }

        btnSendCompleteEmails.setOnClickListener {
            sendRegistrationCompleteEmails()
        }

        btnSendIncompleteReminders.setOnClickListener {
            sendIncompleteRegistrationReminders()
        }
    }

    private fun showClubChooser() {
        val clubs = arrayOf("Cornwall", "Montague")

        AlertDialog.Builder(this)
            .setTitle("View by Club")
            .setItems(clubs) { _, which ->
                openListScreen("club", clubs[which])
            }
            .show()
    }

    private fun showClassChooser() {
        val classes = arrayOf("Class 1", "Class 2", "Class 3")

        AlertDialog.Builder(this)
            .setTitle("View by Class")
            .setItems(classes) { _, which ->
                openListScreen("class", classes[which])
            }
            .show()
    }

    private fun openListScreen(mode: String, value: String) {
        val intent = Intent(this, RegistrationListActivity::class.java).apply {
            putExtra("mode", mode)
            putExtra("value", value)
        }
        startActivity(intent)
    }

    private fun sendWelcomeEmails() {
        Toast.makeText(this, "Sending welcome emails...", Toast.LENGTH_SHORT).show()

        RetrofitClient.api.sendPendingEmails().enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(
                call: Call<SendEmailResponse>,
                response: Response<SendEmailResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Toast.makeText(this@MainActivity, body.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to send welcome emails.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendRegistrationCompleteEmails() {
        Toast.makeText(this, "Sending registration complete emails...", Toast.LENGTH_SHORT).show()

        RetrofitClient.api.sendRegistrationCompleteEmails().enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(
                call: Call<SendEmailResponse>,
                response: Response<SendEmailResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Toast.makeText(this@MainActivity, body.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to send registration complete emails.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendIncompleteRegistrationReminders() {
        Toast.makeText(this, "Sending incomplete registration reminders...", Toast.LENGTH_SHORT).show()

        RetrofitClient.api.sendIncompleteRegistrationReminders().enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(
                call: Call<SendEmailResponse>,
                response: Response<SendEmailResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Toast.makeText(this@MainActivity, body.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to send incomplete reminders.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
