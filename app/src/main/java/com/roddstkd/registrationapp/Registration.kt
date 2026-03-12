package com.roddstkd.registrationapp

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
    val completeEmailSent: String? = "",
    val incompleteReminderSent: String? = "",
    val beltTestInvitationSent: String? = "",
    val classReminderSent: String? = ""
)

data class ActionResponse(
    val success: Boolean = false,
    val message: String = ""
)

data class AttendanceItem(
    val date: String? = "",
    val location: String? = "",
    val className: String? = "",
    val studentName: String? = "",
    val present: String? = "",
    val notes: String? = ""
)

data class BeltTestItem(
    val studentName: String? = "",
    val location: String? = "",
    val className: String? = "",
    val currentBelt: String? = "",
    val eligible: String? = "",
    val invited: String? = "",
    val confirmed: String? = "",
    val paid: String? = "",
    val notes: String? = "",
    val invitationSent: String? = ""
)
