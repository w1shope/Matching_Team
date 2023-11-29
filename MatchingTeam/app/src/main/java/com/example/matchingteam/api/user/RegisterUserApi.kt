package com.example.matchingteam.api.user

import com.example.matchingteam.dto.user.RegisterUserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RegisterUserApi {
    @POST("/api/users")
    fun saveUser(@Body registerUserDto: RegisterUserDto): Call<Long>

    @GET("/api/authenticate")
    fun userEmailAuthentication(@Query("address") address: String): Call<String>

    @POST("/api/authenticate")
    fun userEmailAuthenticationConfirm(@Body authenticateCode: Int): Call<Boolean>
    @GET("/api/users/exist/{email}")
    fun existUserEmail(@Path("email") email: String): Call<Boolean>
}