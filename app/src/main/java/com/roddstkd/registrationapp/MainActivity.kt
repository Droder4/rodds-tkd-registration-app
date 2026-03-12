package com.roddstkd.registrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvCornwall: TextView
    private lateinit var tvMontague: TextView
    private lateinit var tvPendingEmails: TextView
    private lateinit var tvUnpaid: TextView
    private lateinit var tvBeltInvites: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTotal = findViewById(R.id.tvTotalStudents)
        tvCornwall = findViewById(R.id.tvCornwallCount)
        tvMontague = findViewById(R.id.tvMontagueCount)
        tvPendingEmails = findViewById(R.id.tvPendingEmails)
        tvUnpaid = findViewById(R.id.tvUnpaidCount)
        tvBeltInvites = findViewById(R.id.tvBeltInviteCount)

        findViewById<Button>(R.id.btnViewRegistrations).setOnClickListener {
            openRoster("all")
        }

        findViewById<Button>(R.id.btnCornwallRoster).setOnClickListener {
            openRoster("Cornwall")
        }

        findViewById<Button>(R.id.btnMontagueRoster).setOnClickListener {
            openRoster("Montague")
        }

        findViewById<Button>(R.id.btnBeltTesting).setOnClickListener {
            startActivity(Intent(this, BeltTestingActivity::class.java))
        }

        findViewById<Button>(R.id.btnCommunications).setOnClickListener {
            startActivity(Intent(this, CommunicationsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun openRoster(filter: String) {
        val intent = Intent(this, RegistrationListActivity::class.java)
        intent.putExtra("club_filter", filter)
        startActivity(intent)
    }

    private fun loadStats() {
        RetrofitClient.api.getDashboardStats().enqueue(object : Callback<DashboardStats> {
            override fun onResponse(call: Call<DashboardStats>, response: Response<DashboardStats>) {
                val stats = response.body()
                if (response.isSuccessful && stats != null) {
                    tvTotal.text = "Total Students: ${stats.totalStudents}"
                    tvCornwall.text = "Cornwall: ${stats.cornwallStudents}"
                    tvMontague.text = "Montague: ${stats.montagueStudents}"
                    tvPendingEmails.text = "Pending Welcome Emails: ${stats.pendingWelcomeEmails}"
                    tvUnpaid.text = "Unpaid Students: ${stats.unpaidStudents}"
                    tvBeltInvites.text = "Belt Invites Ready: ${stats.beltInviteReady}"
                } else {
                    Toast.makeText(this@MainActivity, "Could not load dashboard.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DashboardStats>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
