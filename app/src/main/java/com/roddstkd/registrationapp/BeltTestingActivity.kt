package com.roddstkd.registrationapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

class BeltTestingActivity : AppCompatActivity() {

    private lateinit var spinnerClub: Spinner
    private lateinit var spinnerClass: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BeltTestingAdapter

    private var allStudents: List<Registration> = emptyList()
    private var filteredStudents: List<Registration> = emptyList()

    private val clubOptions = listOf("Cornwall", "Montague")
    private val classOptions = listOf("Class 1", "Class 2", "Class 3")
    private val beltOptions = listOf(
        "",
        "White Belt",
        "Yellow Stripe",
        "Yellow Belt",
        "Green Stripe",
        "Green Belt",
        "Blue Stripe",
        "Blue Belt",
        "Red Stripe",
        "Red Belt",
        "Black Stripe"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belt_testing)

        spinnerClub = findViewById(R.id.spinnerBeltClub)
        spinnerClass = findViewById(R.id.spinnerBeltClass)
        recyclerView = findViewById(R.id.recyclerViewBeltTesting)

        spinnerClub.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clubOptions)
        spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classOptions)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BeltTestingAdapter { student ->
            showBeltDialog(student)
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnLoadBeltStudents).setOnClickListener {
            applyFilter()
        }

        findViewById<Button>(R.id.btnSendBeltInvitesFromScreen).setOnClickListener {
            sendBeltInvites()
        }

        loadStudents()
    }

    private fun loadStudents() {
        RetrofitClient.api.getRegistrations().enqueue(object : Callback<List<Registration>> {
            override fun onResponse(call: Call<List<Registration>>, response: Response<List<Registration>>) {
                if (response.isSuccessful) {
                    allStudents = response.body().orEmpty()
                    applyFilter()
                } else {
                    Toast.makeText(this@BeltTestingActivity, "Failed to load students.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Registration>>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun applyFilter() {
        val selectedClub = spinnerClub.selectedItem.toString()
        val selectedClass = spinnerClass.selectedItem.toString()

        filteredStudents = allStudents.filter {
            it.location == selectedClub && it.assignedClass == selectedClass
        }

        adapter.submitList(filteredStudents)
        Toast.makeText(this, "Loaded ${filteredStudents.size} students.", Toast.LENGTH_SHORT).show()
    }

    private fun showBeltDialog(student: Registration) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_belt_testing_manage, null)

        val etNotes = view.findViewById<EditText>(R.id.etBeltNotes)
        val etBeltTestDate = view.findViewById<EditText>(R.id.etBeltTestDate)
        val spinnerCurrentBelt = view.findViewById<Spinner>(R.id.spinnerCurrentBeltOnly)
        val spinnerTestingFor = view.findViewById<Spinner>(R.id.spinnerTestingForOnly)

        spinnerCurrentBelt.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, beltOptions)
        spinnerTestingFor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, beltOptions)

        spinnerCurrentBelt.setSelection(beltOptions.indexOf(student.currentBelt ?: "").coerceAtLeast(0))
        spinnerTestingFor.setSelection(beltOptions.indexOf(student.testingFor ?: "").coerceAtLeast(0))
        etBeltTestDate.setText(student.beltTestDate ?: "")
        etNotes.setText(student.studentNotes ?: "")

        AlertDialog.Builder(this)
            .setTitle(student.studentName ?: "Student")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                saveBeltInfo(
                    student = student,
                    currentBelt = spinnerCurrentBelt.selectedItem.toString(),
                    testingFor = spinnerTestingFor.selectedItem.toString(),
                    beltTestDate = etBeltTestDate.text.toString(),
                    notes = etNotes.text.toString()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBeltInfo(
        student: Registration,
        currentBelt: String,
        testingFor: String,
        beltTestDate: String,
        notes: String
    ) {
        val json = JSONObject().apply {
            put("action", "updateStudentManagement")
            put("email", student.email ?: "")
            put("studentName", student.studentName ?: "")
            put("paymentStatus", student.paymentStatus ?: "")
            put("amountPaid", student.amountPaid ?: "")
            put("currentBelt", currentBelt)
            put("testingFor", testingFor)
            put("beltTestDate", beltTestDate)
            put("studentNotes", notes)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.updateStudentManagement(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@BeltTestingActivity, response.body()?.message ?: "Saved", Toast.LENGTH_LONG).show()
                loadStudents()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendBeltInvites() {
        RetrofitClient.api.sendBeltTestInvitations().enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(
                    this@BeltTestingActivity,
                    response.body()?.message ?: "Belt invites sent.",
                    Toast.LENGTH_LONG
                ).show()
                loadStudents()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
