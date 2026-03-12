package com.roddstkd.registrationapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunicationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communications)

        val spinnerClub = findViewById<Spinner>(R.id.spinnerReminderClub)
        val spinnerClass = findViewById<Spinner>(R.id.spinnerReminderClass)
        val etMessage = findViewById<EditText>(R.id.etReminderMessage)

        spinnerClub.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("", "Cornwall", "Montague"))
        spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("", "Class 1", "Class 2", "Class 3"))

        findViewById<Button>(R.id.btnSendWelcomeEmails).setOnClickListener {
            RetrofitClient.api.sendPendingEmails().enqueue(defaultCallback("Welcome emails"))
        }

        findViewById<Button>(R.id.btnSendCompleteEmails).setOnClickListener {
            RetrofitClient.api.sendRegistrationCompleteEmails().enqueue(defaultCallback("Registration complete emails"))
        }

        findViewById<Button>(R.id.btnSendIncompleteReminders).setOnClickListener {
            RetrofitClient.api.sendIncompleteRegistrationReminders().enqueue(defaultCallback("Incomplete reminders"))
        }

        findViewById<Button>(R.id.btnSendClassReminders).setOnClickListener {
            val bodyJson = JSONObject().apply {
                put("action", "sendClassReminders")
                put("location", spinnerClub.selectedItem.toString())
                put("className", spinnerClass.selectedItem.toString())
                put("reminderMessage", etMessage.text.toString())
            }

            val body = bodyJson.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            RetrofitClient.api.sendClassReminders(body).enqueue(defaultCallback("Class reminders"))
        }
    }

    private fun defaultCallback(label: String): Callback<ActionResponse> {
        return object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@CommunicationsActivity, response.body()?.message ?: "$label sent.", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@CommunicationsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
