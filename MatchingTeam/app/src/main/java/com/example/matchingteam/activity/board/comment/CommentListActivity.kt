package com.example.matchingteam.activity.board.comment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.activity.board.ReadBoardActivity
import com.example.matchingteam.api.board.comment.CommentApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityCommentListBinding
import com.example.matchingteam.dto.board.ListBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

class CommentListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findBoards(getLoginEmail())

        binding.buttonMyInfo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        setLayoutClickListener(
            binding.mainLayout1,
            binding.textViewTitle1,
            binding.textViewContent1
        )
    }

    /**
     * 댓글을 작성한 모든 게시물 가져온다
     */
    private fun findBoards(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(CommentApi::class.java)
        val call = api.findCommentAll(email)
        call.enqueue(object : Callback<List<ListBoardDto>> {
            override fun onResponse(
                call: Call<List<ListBoardDto>>,
                response: Response<List<ListBoardDto>>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val boards: List<ListBoardDto>? = response.body()
                        if (boards != null) {
                            for (index in boards.indices) {
                                when (index) {
                                    0 -> dynamicLayout1(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ListBoardDto>>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun dynamicLayout1(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout1.visibility = View.VISIBLE
        binding.textViewTitle1.text = title
        binding.textViewWriter1.text = "테스트"
        binding.textViewContent1.text = content
        binding.textViewCreatedDate1.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt1.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate1.text = createdDate.toString()
        binding.buttonStatus1.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    /**
     * Timestamp -> String
     */
    private fun timestampToStr(time: Timestamp): String {
        return SimpleDateFormat("yyyy.MM.dd").format(time)
    }

    private fun getLoginEmail(): String {
        val sp = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        return sp.getString("loginEmail", null)!!
    }

    private fun setLayoutClickListener(layout: View, titleView: TextView, contentView: TextView) {
        layout.setOnClickListener {
            val intent = Intent(this@CommentListActivity, ReadBoardActivity::class.java)
            intent.putExtra("title", titleView.text.toString())
            intent.putExtra("content", contentView.text.toString())
            startActivity(intent)
        }
    }
}