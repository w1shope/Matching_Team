package com.example.matchingteam.activity.board

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityUpdateBoardBinding
import com.example.matchingteam.dto.board.UpdateBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateBoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        val oldTitle = intent.getStringExtra("title").toString()
        val oldContent = intent.getStringExtra("content").toString()
        updateUI(oldTitle, oldContent)
        binding.buttonCancel.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
        binding.buttonUpdate.setOnClickListener {
            val newTitle = binding.editTextTitle.text.toString()
            val newContent = binding.editTextContent.text.toString()
            updateBoard(oldTitle, oldContent, newTitle, newContent)
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
     * 게시물 수정
     */
    private fun updateBoard(
        oldTitle: String,
        oldContent: String,
        newTitle: String,
        newContent: String
    ) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Boolean> =
            api.updateBoard(UpdateBoardDto(oldTitle, oldContent, newTitle, newContent))
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body()?.equals(true)!!) {
                        updateUI(newTitle, newContent)
                        Toast.makeText(applicationContext, "게시물 수정이 완료되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent: Intent = Intent(this@UpdateBoardActivity, BoardActivity::class.java)
//                            finish()
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