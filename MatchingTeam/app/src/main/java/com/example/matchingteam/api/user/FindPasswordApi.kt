package com.example.matchingteam.api.user

import com.example.matchingteam.dto.user.UserEmailAuthenticateDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FindPasswordApi {
    @GET("/api/users/password")
    fun findPassword(@Query("email") email: String): Call<UserEmailAuthenticateDto>

    @POST("/api/users/password")
    fun authenticate(@Body authenticateCode: Int): Call<Boolean>

    @GET("/api/users/password/{email}")
    fun getUserPassword(@Path("email") email: String): Call<String>

}