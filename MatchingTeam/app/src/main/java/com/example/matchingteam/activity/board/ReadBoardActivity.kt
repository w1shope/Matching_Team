package com.example.matchingteam.activity.board

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.api.board.comment.CommentApi
import com.example.matchingteam.api.user.FindUserApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityReadBoardBinding
import com.example.matchingteam.dto.board.comment.EnrolCommentDto
import com.example.matchingteam.dto.board.comment.FindCommentDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp


class ReadBoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadBoardBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        val title = intent.getStringExtra("title")!!
        val content = intent.getStringExtra("content")!!
        updateUI(title, content)
        initUpdateUIComments(title, content)
        checkIfCurrentUserIsAuthor(title, content)

        binding.buttonBoardList.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("content", content)
            startActivity(intent)
        }

        binding.buttonUpdate.setOnClickListener {
            val intent = Intent(this, UpdateBoardActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("content", content)
            startActivity(intent)
        }

        binding.buttonDelete.setOnClickListener {
            deleteBoard(title, content, getLoginUserEmail())
        }

        binding.buttonEnrolBtn.setOnClickListener {
            if (binding.buttonEnrolBtn.text == "등록") {
                enrolComments(
                    getLoginUserEmail(),
                    binding.editTextComment.text.toString(),
                    title,
                    content
                )
            } else {
                deleteComment(binding.editTextComment.text.toString(), getLoginEmail())
            }
        }
    }

    /**
     * 게시물 정보를 업데이트 하는 메서드
     */
    private fun updateUI(title: String, content: String) {
        binding.editTextTitle.setText(title)
        binding.editTextContent.setText(content)
    }

    /**
     * 게시물 작성자가 본인인지 확인하는 메서드
    -> 게시물 수정, 삭제 버튼 생성에 필요
     */
    private fun checkIfCurrentUserIsAuthor(title: String, content: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: FindUserApi = retrofit.create(FindUserApi::class.java)
        val call: Call<Boolean> = api.isWriter(getLoginUserEmail(), title, content)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val isWriter: Boolean = response.body()!!
                        if (isWriter) {
                            binding.buttonUpdate.visibility = View.VISIBLE
                            binding.buttonDelete.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
            }
        })
    }

    /**
     * 게시물 삭제
     */
    private fun deleteBoard(title: String, content: String, loginEmail: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Boolean> = api.deleteBoard(title, content, loginEmail)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body()?.equals(true)!!) {
                        Toast.makeText(applicationContext, "게시물이 삭제되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@ReadBoardActivity, BoardActivity::class.java)
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

    private fun getLoginUserEmail(): String {
        val sp: SharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
        return sp.getString("loginEmail", null)!!
    }

    /**
     * 댓글 등록 API
     */
    private fun enrolComments(
        email: String,
        commentContent: String,
        boardTitle: String,
        boardContent: String
    ) {
        val retrofit = RetrofitConnection.getInstance()
        val api: CommentApi = retrofit.create(CommentApi::class.java)
        val call: Call<String> = api.enrolComment(
            EnrolCommentDto(
                email,
                commentContent,
                boardTitle,
                boardContent
            )
        )
        call.enqueue(object : Callback<String> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(applicationContext, "댓글이 정상적으로 등록되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        val intent = getIntent()
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun updateUIComments(writer: String, content: String, createdDate: Timestamp) {
        binding.textViewCommentWriter.text = writer
        binding.textViewCommentWriter.visibility = View.VISIBLE
        binding.editTextComment.setText(content)
        binding.editTextComment.visibility = View.VISIBLE
        binding.textViewCommentCreatedDate.text = createdDate.toString()
        binding.textViewCommentCreatedDate.visibility = View.VISIBLE
        if (binding.buttonEnrolBtn.text == "등록")
            binding.buttonEnrolBtn.text = "삭제"
        else
            binding.buttonEnrolBtn.text = "등록"
    }

    private fun initUpdateUIComments(title: String, content: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(CommentApi::class.java)
        val call = api.findComment(title, content)
        call.enqueue(object : Callback<FindCommentDto> {
            override fun onResponse(
                call: Call<FindCommentDto>,
                response: Response<FindCommentDto>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        updateUIComments(
                            response.body()!!.name,
                            response.body()!!.content,
                            response.body()!!.createdDate
                        )
                    }
                }
            }

            override fun onFailure(call: Call<FindCommentDto>, t: Throwable) {
            }
        })
    }

    private fun deleteComment(content: String, loginEmail: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(CommentApi::class.java)
        val call = api.deleteComment(content, loginEmail)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body() == false) {
                            Toast.makeText(applicationContext, "댓글 작성자만 삭제할 수 있습니다", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "댓글을 정상적으로 삭제하였습니다",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.editTextComment.visibility = View.GONE
                            binding.textViewCommentWriter.visibility = View.GONE
                            binding.textViewCommentCreatedDate.visibility = View.GONE
                        }

                        val intent = getIntent()
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
            }
        })
    }

    private fun getLoginEmail(): String {
        val sp = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        return sp.getString("loginEmail", null)!!
    }
}