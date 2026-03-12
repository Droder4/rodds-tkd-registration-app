package com.roddstkd.registrationapp

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("exec")
    fun getRegistrations(
        @Query("action") action: String = "getRegistrations"
    ): Call<List<Registration>>

    @GET("exec")
    fun getByClub(
        @Query("action") action: String = "getByClub",
        @Query("club") club: String
    ): Call<List<Registration>>

    @GET("exec")
    fun getByClass(
        @Query("action") action: String = "getByClass",
        @Query("className") className: String
    ): Call<List<Registration>>

    @GET("exec")
    fun sendPendingEmails(
        @Query("action") action: String = "sendPendingEmails"
    ): Call<ActionResponse>

    @GET("exec")
    fun sendRegistrationCompleteEmails(
        @Query("action") action: String = "sendRegistrationCompleteEmails"
    ): Call<ActionResponse>

    @GET("exec")
    fun sendIncompleteRegistrationReminders(
        @Query("action") action: String = "sendIncompleteRegistrationReminders"
    ): Call<ActionResponse>

    @GET("exec")
    fun getAttendance(
        @Query("action") action: String = "getAttendance",
        @Query("location") location: String = "",
        @Query("className") className: String = "",
        @Query("date") date: String = ""
    ): Call<List<AttendanceItem>>

    @GET("exec")
    fun getBeltTesting(
        @Query("action") action: String = "getBeltTesting"
    ): Call<List<BeltTestItem>>

    @GET("exec")
    fun markWelcomeEmailSent(
        @Query("action") action: String = "markWelcomeEmailSent",
        @Query("email") email: String,
        @Query("studentName") studentName: String
    ): Call<ActionResponse>

    @POST("exec")
    fun saveAttendance(
        @Body body: RequestBody
    ): Call<ActionResponse>

    @POST("exec")
    fun saveBeltTesting(
        @Body body: RequestBody
    ): Call<ActionResponse>

    @POST("exec")
    fun updateStudentNotes(
        @Body body: RequestBody
    ): Call<ActionResponse>

    @POST("exec")
    fun sendBeltTestInvitations(
        @Body body: RequestBody
    ): Call<ActionResponse>

    @POST("exec")
    fun sendClassReminders(
        @Body body: RequestBody
    ): Call<ActionResponse>
}
