package com.roddstkd.registrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStudents).setOnClickListener {
            startActivity(Intent(this, RegistrationListActivity::class.java))
        }

        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        findViewById<Button>(R.id.btnBeltTesting).setOnClickListener {
            startActivity(Intent(this, BeltTestingActivity::class.java))
        }

        findViewById<Button>(R.id.btnCommunications).setOnClickListener {
            startActivity(Intent(this, CommunicationsActivity::class.java))
        }
    }
}
