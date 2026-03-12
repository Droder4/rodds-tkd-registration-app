package com.roddstkd.registrationapp

data class DashboardStats(
    val totalStudents: Int = 0,
    val cornwallStudents: Int = 0,
    val montagueStudents: Int = 0,
    val pendingWelcomeEmails: Int = 0,
    val unpaidStudents: Int = 0,
    val beltInviteReady: Int = 0
)

data class Registration(
    val timestamp: String? = "",
    val studentType: String? = "",
    val location: String? = "",
    val email: String? = "",
    val parentName: String? = "",
    val contactNumber: String? = "",
    val studentName: String? = "",
    val studentAge: String? = "",
    val birthdate: String? = "",
    val grade: String? = "",
    val assignedClass: String? = "",
    val emailStatus: String? = "",
    val notes: String? = "",
    val registrationStatus: String? = "",
    val paymentStatus: String? = "",
    val amountPaid: String? = "",
    val currentBelt: String? = "",
    val testingFor: String? = "",
    val beltInviteStatus: String? = "",
    val studentNotes: String? = ""
)

data class ActionResponse(
    val success: Boolean = false,
    val message: String = ""
)
