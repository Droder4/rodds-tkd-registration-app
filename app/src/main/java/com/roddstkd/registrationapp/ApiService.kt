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
    fun getBeltTesting(
        @Query("action") action: String = "getBeltTesting",
        @Query("location") location: String = "",
        @Query("className") className: String = ""
    ): Call<List<BeltTestItem>>

    @POST("exec")
    fun saveBeltTesting(
        @Body body: RequestBody
    ): Call<ActionResponse>

    @GET("exec")
    fun sendBeltTestInvitations(
        @Query("action") action: String = "sendBeltTestInvitations",
        @Query("location") location: String = "",
        @Query("className") className: String = ""
    ): Call<ActionResponse>
}
