package com.example.matchingteam.activity.board

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityEnrolBoardBinding
import com.example.matchingteam.dto.board.EnrolBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnrolBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityEnrolBoardBinding = ActivityEnrolBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonEnrol.setOnClickListener {
            val sp: SharedPreferences =
                getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
            val title: String = binding.editTextTitle.text.toString().trim()
            val content: String = binding.editTextContent.text.toString().trim()
            val writerEmail: String = sp.getString("loginEmail", null).toString().trim()
            enrolBoard(title, content, writerEmail)
        }
        binding.imageViewClose.setOnClickListener {
            finish()
        }
    }

    private fun enrolBoard(title: String, content: String, writerEmail: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Boolean> =
            api.enrolBoard(EnrolBoardDto(title, content, writerEmail, 1))
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(applicationContext, "정상적으로 등록되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent: Intent =
                                Intent(this@EnrolBoardActivity, BoardActivity::class.java)
                            startActivity(intent)
                        }, 1500)
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}