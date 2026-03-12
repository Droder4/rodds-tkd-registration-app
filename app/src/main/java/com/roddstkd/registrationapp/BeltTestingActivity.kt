package com.roddstkd.registrationapp

import android.os.Bundle
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

        btnLoad.setOnClickListener { loadBeltTesting() }
        btnSave.setOnClickListener { saveBeltTesting() }
        btnSendInvites.setOnClickListener { sendInvitations() }

        loadBeltTesting()
    }

    private fun loadBeltTesting() {
        RetrofitClient.api.getBeltTesting().enqueue(object : Callback<List<BeltTestItem>> {
            override fun onResponse(call: Call<List<BeltTestItem>>, response: Response<List<BeltTestItem>>) {
                if (response.isSuccessful) {
                    items = response.body().orEmpty().toMutableList()
                    listView.adapter = BeltTestingAdapter()
                } else {
                    Toast.makeText(this@BeltTestingActivity, "Failed to load belt testing.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<BeltTestItem>>, t: Throwable) {
                Toast.makeText(this@BeltTestingActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveBeltTesting() {
        val jsonItems = JSONArray()

        for (i in items.indices) {
            val rowView = listView.getChildAt(i - listView.firstVisiblePosition) ?: continue

            val spinnerTestingFor = rowView.findViewById<Spinner>(R.id.spinnerTestingFor)
            val cbEligible = rowView.findViewById<CheckBox>(R.id.cbEligible)
            val cbInvited = rowView.findViewById<CheckBox>(R.id.cbInvited)
            val cbConfirmed = rowView.findViewById<CheckBox>(R.id.cbConfirmed)
            val cbPaid = rowView.findViewById<CheckBox>(R.id.cbPaid)
            val etCurrentBelt = rowView.findViewById<EditText>(R.id.etCurrentBelt)
            val etNotes = rowView.findViewById<EditText>(R.id.etBeltNotes)

            val testingFor = spinnerTestingFor.selectedItem.toString()
            val fee = getFeeForBelt(testingFor)

            val obj = JSONObject().apply {
                put("studentName", items[i].studentName ?: "")
                put("location", items[i].location ?: "")
                put("className", items[i].className ?: "")
                put("currentBelt", etCurrentBelt.text.toString())
                put("testingFor", testingFor)
                put("eligible", if (cbEligible.isChecked) "Yes" else "No")
                put("invited", if (cbInvited.isChecked) "Yes" else "No")
                put("confirmed", if (cbConfirmed.isChecked) "Yes" else "No")
                put("paid", if (cbPaid.isChecked) "Yes" else "No")
                put("fee", fee)
                put("notes", etNotes.text.toString())
                put("invitationSent", items[i].invitationSent ?: "")
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
                Toast.makeText(this@BeltTestingActivity, response.body()?.message ?: "Sent", Toast.LENGTH_LONG).show()
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

    inner class BeltTestingAdapter : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
            val view = layoutInflater.inflate(R.layout.item_belt_testing, parent, false)
            val item = items[position]

            val tvStudentName = view.findViewById<TextView>(R.id.tvBeltStudentName)
            val tvStudentInfo = view.findViewById<TextView>(R.id.tvBeltStudentInfo)
            val etCurrentBelt = view.findViewById<EditText>(R.id.etCurrentBelt)
            val spinnerTestingFor = view.findViewById<Spinner>(R.id.spinnerTestingFor)
            val tvFee = view.findViewById<TextView>(R.id.tvBeltFee)
            val cbEligible = view.findViewById<CheckBox>(R.id.cbEligible)
            val cbInvited = view.findViewById<CheckBox>(R.id.cbInvited)
            val cbConfirmed = view.findViewById<CheckBox>(R.id.cbConfirmed)
            val cbPaid = view.findViewById<CheckBox>(R.id.cbPaid)
            val etNotes = view.findViewById<EditText>(R.id.etBeltNotes)

            tvStudentName.text = item.studentName ?: ""
            tvStudentInfo.text = "${item.location} • ${item.className}"

            etCurrentBelt.setText(item.currentBelt ?: "")
            etNotes.setText(item.notes ?: "")

            spinnerTestingFor.adapter = ArrayAdapter(
                this@BeltTestingActivity,
                android.R.layout.simple_spinner_dropdown_item,
                beltOptions
            )

            val selectedIndex = beltOptions.indexOf(item.testingFor ?: "").let { if (it >= 0) it else 0 }
            spinnerTestingFor.setSelection(selectedIndex)

            tvFee.text = "Fee: ${getFeeForBelt(item.testingFor ?: "")}"

            spinnerTestingFor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view2: android.view.View?, pos: Int, id: Long) {
                    val selectedBelt = beltOptions[pos]
                    tvFee.text = "Fee: ${getFeeForBelt(selectedBelt)}"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            cbEligible.isChecked = item.eligible == "Yes"
            cbInvited.isChecked = item.invited == "Yes"
            cbConfirmed.isChecked = item.confirmed == "Yes"
            cbPaid.isChecked = item.paid == "Yes"

            return view
        }
    }
}
