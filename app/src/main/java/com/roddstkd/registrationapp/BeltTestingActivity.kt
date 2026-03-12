package com.roddstkd.registrationapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeltTestingActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var items: List<BeltTestItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belt_testing)

        listView = findViewById(R.id.listBeltTesting)

        findViewById<Button>(R.id.btnLoadBeltTesting).setOnClickListener {
            loadBeltTesting()
        }

        findViewById<Button>(R.id.btnSendBeltInvites).setOnClickListener {
            sendInvitations()
        }
    }

    private fun loadBeltTesting() {
        RetrofitClient.api.getBeltTesting().enqueue(object : Callback<List<BeltTestItem>> {
            override fun onResponse(call: Call<List<BeltTestItem>>, response: Response<List<BeltTestItem>>) {
                if (response.isSuccessful) {
                    items = response.body().orEmpty()
                    val lines = items.map {
                        "${it.studentName} - ${it.className} - Eligible: ${it.eligible} - Invited: ${it.invited}"
                    }
                    listView.adapter = ArrayAdapter(this@BeltTestingActivity, android.R.layout.simple_list_item_1, lines)
                } else {
                    Toast.makeText(this@BeltTestingActivity, "Failed to load belt testing.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<BeltTestItem>>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendInvitations() {
        val bodyJson = JSONObject().apply {
            put("action", "sendBeltTestInvitations")
            put("location", "")
            put("className", "")
        }

        val body = bodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.sendBeltTestInvitations(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@BeltTestingActivity, response.body()?.message ?: "Sent", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
