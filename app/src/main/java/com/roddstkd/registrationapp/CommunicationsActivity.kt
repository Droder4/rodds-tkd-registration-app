package com.roddstkd.registrationapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunicationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communications)

        findViewById<Button>(R.id.btnSendWelcomeEmails).setOnClickListener {
            RetrofitClient.api.sendPendingEmails().enqueue(defaultCallback())
        }

        findViewById<Button>(R.id.btnSendBeltInvites).setOnClickListener {
            RetrofitClient.api.sendBeltTestInvitations().enqueue(defaultCallback())
        }
    }

    private fun defaultCallback(): Callback<ActionResponse> {
        return object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(
                    this@CommunicationsActivity,
                    response.body()?.message ?: "Done",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@CommunicationsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
