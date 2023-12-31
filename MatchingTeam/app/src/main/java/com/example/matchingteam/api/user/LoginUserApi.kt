package com.example.matchingteam.api.user

import com.example.matchingteam.dto.user.LoginUserDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginUserApi {
    @GET("/api/users")
    fun findByUserEmailAndPassword(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<LoginUserDto>
}