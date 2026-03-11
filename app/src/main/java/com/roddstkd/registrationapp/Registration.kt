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
    val membershipNoticeAgreed: String? = "",
    val lateFeeNoticeAgreed: String? = "",
    val emailStatus: String? = "",
    val emailSentDate: String? = "",
    val notes: String? = ""
)

data class SendEmailResponse(
    val success: Boolean,
    val message: String
)
