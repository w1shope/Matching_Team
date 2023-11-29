package com.example.matchingteam.dto.board

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ListBoardDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("name") val name: String,
    @SerializedName("content") val content: String,
    @SerializedName("viewCnt") val viewCnt: Int,
    @SerializedName("createdDate") val createdDate: Timestamp,
    @SerializedName("status") val status: Int
)
