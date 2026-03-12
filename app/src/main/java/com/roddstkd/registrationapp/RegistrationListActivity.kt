package com.roddstkd.registrationapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
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

class RegistrationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrationAdapter
    private var items: List<Registration> = emptyList()
    private var clubFilter: String = "all"

    private val paymentOptions = listOf("Unpaid", "Partial", "Paid in Full")
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
        setContentView(R.layout.activity_registration_list)

        clubFilter = intent.getStringExtra("club_filter") ?: "all"

        recyclerView = findViewById(R.id.recyclerViewRegistrations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RegistrationAdapter { item ->
            showManageDialog(item)
        }
        recyclerView.adapter = adapter

        loadStudents()
    }

    private fun loadStudents() {
        val call = if (clubFilter == "all") {
            RetrofitClient.api.getRegistrations()
        } else {
            RetrofitClient.api.getByClub(club = clubFilter)
        }

        call.enqueue(object : Callback<List<Registration>> {
            override fun onResponse(call: Call<List<Registration>>, response: Response<List<Registration>>) {
                if (response.isSuccessful) {
                    items = response.body().orEmpty()
                    adapter.submitList(items)
                } else {
                    Toast.makeText(this@RegistrationListActivity, "Failed to load students.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Registration>>, t: Throwable) {
                Toast.makeText(this@RegistrationListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showManageDialog(item: Registration) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_manage_student, null)

        val etAmountPaid = view.findViewById<EditText>(R.id.etAmountPaid)
        val etNotes = view.findViewById<EditText>(R.id.etStudentNotes)
        val spinnerPayment = view.findViewById<Spinner>(R.id.spinnerPaymentStatus)
        val spinnerCurrentBelt = view.findViewById<Spinner>(R.id.spinnerCurrentBelt)
        val spinnerTestingFor = view.findViewById<Spinner>(R.id.spinnerTestingFor)

        etAmountPaid.setText(item.amountPaid ?: "")
        etNotes.setText(item.studentNotes ?: "")

        spinnerPayment.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentOptions)
        spinnerCurrentBelt.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, beltOptions)
        spinnerTestingFor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, beltOptions)

        spinnerPayment.setSelection(paymentOptions.indexOf(item.paymentStatus ?: "Unpaid").coerceAtLeast(0))
        spinnerCurrentBelt.setSelection(beltOptions.indexOf(item.currentBelt ?: "").coerceAtLeast(0))
        spinnerTestingFor.setSelection(beltOptions.indexOf(item.testingFor ?: "").coerceAtLeast(0))

        AlertDialog.Builder(this)
            .setTitle(item.studentName ?: "Student")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                saveStudent(
                    item = item,
                    paymentStatus = spinnerPayment.selectedItem.toString(),
                    amountPaid = etAmountPaid.text.toString(),
                    currentBelt = spinnerCurrentBelt.selectedItem.toString(),
                    testingFor = spinnerTestingFor.selectedItem.toString(),
                    notes = etNotes.text.toString()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveStudent(
        item: Registration,
        paymentStatus: String,
        amountPaid: String,
        currentBelt: String,
        testingFor: String,
        notes: String
    ) {
        val json = JSONObject().apply {
            put("action", "updateStudentManagement")
            put("email", item.email ?: "")
            put("studentName", item.studentName ?: "")
            put("paymentStatus", paymentStatus)
            put("amountPaid", amountPaid)
            put("currentBelt", currentBelt)
            put("testingFor", testingFor)
            put("studentNotes", notes)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.updateStudentManagement(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@RegistrationListActivity, response.body()?.message ?: "Saved", Toast.LENGTH_LONG).show()
                loadStudents()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@RegistrationListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
