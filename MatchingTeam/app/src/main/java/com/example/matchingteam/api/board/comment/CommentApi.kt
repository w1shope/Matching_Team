package com.example.matchingteam.api.board.comment

import com.example.matchingteam.domain.board.Board
import com.example.matchingteam.dto.board.ListBoardDto
import com.example.matchingteam.dto.board.comment.EnrolCommentDto
import com.example.matchingteam.dto.board.comment.FindCommentDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {
    @POST("/api/comments")
    fun enrolComment(@Body commentDto: EnrolCommentDto): Call<String>

    @GET("/api/comments/{title}/{content}")
    fun findComment(@Path("title") title: String, @Path("content") content: String): Call<FindCommentDto>

    @DELETE("/api/comments/{content}")
    fun deleteComment(@Path("content") comment: String, @Query("loginEmail") loginEmail: String): Call<Boolean>

    @GET("/api/comments/{email}")
    fun findCommentAll(@Path("email") email: String): Call<List<ListBoardDto>>
    @GET("/api/comments/writer")
    fun findCommentWriter(@Query("title") titie: String, @Query("content") content: String): Call<String>
}