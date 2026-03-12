package com.roddstkd.registrationapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
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
    private lateinit var btnLoad: Button
    private lateinit var btnSave: Button
    private lateinit var btnSendInvites: Button

    private var items: MutableList<BeltTestItem> = mutableListOf()
    private lateinit var adapter: BeltTestingListAdapter

    private val beltOptions = listOf(
        "",
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

        listView = findViewById(R.id.listBeltTesting)
        btnLoad = findViewById(R.id.btnLoadBeltTesting)
        btnSave = findViewById(R.id.btnSaveBeltTesting)
        btnSendInvites = findViewById(R.id.btnSendBeltInvites)

        adapter = BeltTestingListAdapter()
        listView.adapter = adapter

        btnLoad.setOnClickListener { loadBeltTesting() }
        btnSave.setOnClickListener { saveBeltTesting() }
        btnSendInvites.setOnClickListener { sendInvitations() }

        listView.setOnItemClickListener { _, _, position, _ ->
            showEditDialog(position)
        }

        loadBeltTesting()
    }

    private fun loadBeltTesting() {
        RetrofitClient.api.getBeltTesting().enqueue(object : Callback<List<BeltTestItem>> {
            override fun onResponse(call: Call<List<BeltTestItem>>, response: Response<List<BeltTestItem>>) {
                if (response.isSuccessful) {
                    items = response.body().orEmpty().toMutableList()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@BeltTestingActivity, "Belt testing list loaded.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BeltTestingActivity, "Failed to load belt testing list.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<BeltTestItem>>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showEditDialog(position: Int) {
        val item = items[position]
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_belt_testing_edit, null)

        val tvStudent = view.findViewById<TextView>(R.id.tvDialogStudent)
        val etCurrentBelt = view.findViewById<EditText>(R.id.etDialogCurrentBelt)
        val spinnerTestingFor = view.findViewById<Spinner>(R.id.spinnerDialogTestingFor)
        val tvFee = view.findViewById<TextView>(R.id.tvDialogFee)
        val cbEligible = view.findViewById<CheckBox>(R.id.cbDialogEligible)
        val cbInvited = view.findViewById<CheckBox>(R.id.cbDialogInvited)
        val cbConfirmed = view.findViewById<CheckBox>(R.id.cbDialogConfirmed)
        val cbPaid = view.findViewById<CheckBox>(R.id.cbDialogPaid)
        val etNotes = view.findViewById<EditText>(R.id.etDialogNotes)

        tvStudent.text = "${item.studentName} • ${item.location} • ${item.className}"
        etCurrentBelt.setText(item.currentBelt ?: "")
        etNotes.setText(item.notes ?: "")

        spinnerTestingFor.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            beltOptions
        )

        val selectedIndex = beltOptions.indexOf(item.testingFor ?: "").let { if (it >= 0) it else 0 }
        spinnerTestingFor.setSelection(selectedIndex)

        tvFee.text = "Fee: ${getFeeForBelt(item.testingFor ?: "")}"

        spinnerTestingFor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                tvFee.text = "Fee: ${getFeeForBelt(beltOptions[pos])}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        cbEligible.isChecked = item.eligible == "Yes"
        cbInvited.isChecked = item.invited == "Yes"
        cbConfirmed.isChecked = item.confirmed == "Yes"
        cbPaid.isChecked = item.paid == "Yes"

        AlertDialog.Builder(this)
            .setTitle("Edit Belt Test")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val updated = item.copy(
                    currentBelt = etCurrentBelt.text.toString(),
                    testingFor = spinnerTestingFor.selectedItem.toString(),
                    eligible = if (cbEligible.isChecked) "Yes" else "No",
                    invited = if (cbInvited.isChecked) "Yes" else "No",
                    confirmed = if (cbConfirmed.isChecked) "Yes" else "No",
                    paid = if (cbPaid.isChecked) "Yes" else "No",
                    fee = getFeeForBelt(spinnerTestingFor.selectedItem.toString()),
                    notes = etNotes.text.toString()
                )

                items[position] = updated
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBeltTesting() {
        val jsonItems = JSONArray()

        items.forEach { item ->
            val obj = JSONObject().apply {
                put("studentName", item.studentName ?: "")
                put("location", item.location ?: "")
                put("className", item.className ?: "")
                put("currentBelt", item.currentBelt ?: "")
                put("testingFor", item.testingFor ?: "")
                put("eligible", item.eligible ?: "No")
                put("invited", item.invited ?: "No")
                put("confirmed", item.confirmed ?: "No")
                put("paid", item.paid ?: "No")
                put("fee", item.fee ?: "")
                put("notes", item.notes ?: "")
                put("invitationSent", item.invitationSent ?: "")
            }
            jsonItems.put(obj)
        }

        val bodyJson = JSONObject().apply {
            put("action", "saveBeltTesting")
            put("items", jsonItems)
        }

        val body = bodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        RetrofitClient.api.saveBeltTesting(body).enqueue(object : Callback<ActionResponse> {
            override fun onResponse(call: Call<ActionResponse>, response: Response<ActionResponse>) {
                Toast.makeText(this@BeltTestingActivity, response.body()?.message ?: "Saved", Toast.LENGTH_LONG).show()
                loadBeltTesting()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
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
                Toast.makeText(this@BeltTestingActivity, response.body()?.message ?: "Invitations sent", Toast.LENGTH_LONG).show()
                loadBeltTesting()
            }

            override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getFeeForBelt(belt: String): String {
        return when (belt) {
            "Yellow Stripe" -> "$50"
            "Yellow Belt" -> "$50"
            "Green Stripe" -> "$55"
            "Green Belt" -> "$55"
            "Blue Stripe" -> "$60"
            "Blue Belt" -> "$65"
            "Red Stripe" -> "$70"
            "Red Belt" -> "$80"
            "Black Stripe" -> "$90"
            else -> ""
        }
    }

    inner class BeltTestingListAdapter : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
            val view = convertView ?: layoutInflater.inflate(R.layout.item_belt_testing_simple, parent, false)
            val item = items[position]

            val tvName = view.findViewById<TextView>(R.id.tvSimpleStudentName)
            val tvInfo = view.findViewById<TextView>(R.id.tvSimpleStudentInfo)
            val tvStatus = view.findViewById<TextView>(R.id.tvSimpleStudentStatus)

            tvName.text = item.studentName ?: ""
            tvInfo.text = "${item.location} • ${item.className}\nCurrent: ${item.currentBelt ?: ""}\nTesting For: ${item.testingFor ?: ""}\nFee: ${item.fee ?: ""}"
            tvStatus.text = "Eligible: ${item.eligible}  Invited: ${item.invited}  Confirmed: ${item.confirmed}  Paid: ${item.paid}"

            return view
        }
    }
}
