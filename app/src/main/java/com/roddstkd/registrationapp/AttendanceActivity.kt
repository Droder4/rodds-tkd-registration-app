package com.roddstkd.registrationapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceActivity : AppCompatActivity() {

    private lateinit var spinnerClub: Spinner
    private lateinit var spinnerClass: Spinner
    private lateinit var etDate: EditText
    private lateinit var listView: ListView

    private var registrations: List<Registration> = emptyList()
    private val presentStates = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        spinnerClub = findViewById(R.id.spinnerAttendanceClub)
        spinnerClass = findViewById(R.id.spinnerAttendanceClass)
        etDate = findViewById(R.id.etAttendanceDate)
        listView = findViewById(R.id.listAttendanceStudents)

        spinnerClub.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Cornwall", "Montague"))
        spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Class 1", "Class 2", "Class 3"))

        findViewById<Button>(R.id.btnLoadAttendance).setOnClickListener {
            loadStudents()
        }

        findViewById<Button>(R.id.btnSaveAttendance).setOnClickListener {
            saveAttendance()
        }
    }

    private fun loadStudents() {
        RetrofitClient.api.getRegistrations().enqueue(object : Callback<List<Registration>> {
            override fun onResponse(call: Call<List<Registration>>, response: Response<List<Registration>>) {
                if (response.isSuccessful) {
                    val club = spinnerClub.selectedItem.toString()
                    val className = spinnerClass.selectedItem.toString()

                    registrations = response.body().orEmpty().filter {
                        it.location == club && it.assignedClass == className
                    }

                    presentStates.clear()
                    registrations.forEach { presentStates[it.studentName ?: ""] = true }

                    val names = registrations.map { "${it.studentName} - Present" }
                    listView.adapter = ArrayAdapter(this@AttendanceActivity, android.R.layout.simple_list_item_multiple_choice, names)
                    listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                    for (i in registrations.indices) {
                        listView.setItemChecked(i, true)
                    }
                }
            }

            override fun onFailure(call: Call<List<Registration>>, t: Throwable) {
                Toast.makeText(this@AttendanceActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveAttendance() {
        val date = etDate.text.toString().trim()
        val club = spinnerClub.selectedItem.toString()
        val className = spinnerClass.selectedItem.toString()

        val items = JSONArray()
        for (i in registrations.indices) {
            val item = registrations[i]
            val present = listView.isItemChecked(i)
            val obj = JSONObject().apply {
                put("date", date)
                put("location", club)
                put("className", className)
                put("studentName", item.studentName ?: "")
                put("present", if (present) "Yes" else "No")
                put("notes", "")
            }
            items.put(obj)
        }

        val bodyJson = JSONObject().apply {
            put("action", "saveAttendance")
            put("items", items)
        }

        val body = bodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.saveAttendance(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@AttendanceActivity, response.body()?.message ?: "Attendance saved.", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@AttendanceActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
