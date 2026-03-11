package com.roddstkd.registrationapp

import retrofit2.Call
import retrofit2.http.GET
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
    ): Call<SendEmailResponse>

    @GET("exec")
    fun sendRegistrationCompleteEmails(
        @Query("action") action: String = "sendRegistrationCompleteEmails"
    ): Call<SendEmailResponse>

    @GET("exec")
    fun sendIncompleteRegistrationReminders(
        @Query("action") action: String = "sendIncompleteRegistrationReminders"
    ): Call<SendEmailResponse>
}
