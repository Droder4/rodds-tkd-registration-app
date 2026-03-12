package com.roddstkd.registrationapp

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("exec")
    fun getDashboardStats(
        @Query("action") action: String = "getDashboardStats"
    ): Call<DashboardStats>

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
    fun sendPendingEmails(
        @Query("action") action: String = "sendPendingEmails"
    ): Call<ActionResponse>

    @GET("exec")
    fun sendBeltTestInvitations(
        @Query("action") action: String = "sendBeltTestInvitations"
    ): Call<ActionResponse>

    @POST("exec")
    fun updateStudentManagement(
        @Body body: RequestBody
    ): Call<ActionResponse>
}
