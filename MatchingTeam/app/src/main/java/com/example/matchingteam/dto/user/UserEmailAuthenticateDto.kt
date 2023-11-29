package com.example.matchingteam.dto.user

import com.google.gson.annotations.SerializedName

data class UserEmailAuthenticateDto(
    @SerializedName("authenticateCode") val authenticateCode: Int
)
