package com.example.matchingteam.activity.myinfo

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.R
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.activity.board.ReadBoardActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityEnrolBoardListBinding
import com.example.matchingteam.domain.board.Board
import com.example.matchingteam.dto.board.BoardStatusDto
import com.example.matchingteam.dto.board.ListBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

class EnrolBoardListActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnrolBoardListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolBoardListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        boards(getLoginUserEmail()!!.trim())

        binding.buttonBoardList.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        setUpButtonClick(binding.buttonStatus1, binding.textViewTitle1, binding.textViewContent1)
        setUpButtonClick(binding.buttonStatus2, binding.textViewTitle2, binding.textViewContent2)
        setUpButtonClick(binding.buttonStatus3, binding.textViewTitle3, binding.textViewContent3)
        setLayoutClickListener(binding.mainLayout1, binding.textViewTitle1, binding.textViewContent1)
        setLayoutClickListener(binding.mainLayout2, binding.textViewTitle2, binding.textViewContent2)
        setLayoutClickListener(binding.mainLayout3, binding.textViewTitle3, binding.textViewContent3)
    }

    /**
     * 모든 프로젝트 모집글 가져오기
     */
    private fun boards(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<List<ListBoardDto>> = api.userBoards(email)
        call.enqueue(object : Callback<List<ListBoardDto>?> {
            override fun onResponse(
                call: Call<List<ListBoardDto>?>,
                response: Response<List<ListBoardDto>?>
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

                                    1 -> dynamicLayout2(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )

                                    else -> dynamicLayout3(
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

            override fun onFailure(call: Call<List<ListBoardDto>?>, t: Throwable) {}
        })
    }

    /**
     * 특정 게시물 가져오기
     */
    private fun findBoard(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int
    ) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Board> = api.findBoard(title, content, createdDate, viewCnt)
        call.enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val sp: SharedPreferences =
                            getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
                        val loginEmail: String? = sp.getString("loginEmail", null)
                        val intent =
                            Intent(this@EnrolBoardListActivity, ReadBoardActivity::class.java)
                        intent.putExtra("title", title)
                        intent.putExtra("content", content)
                        intent.putExtra("loginEmail", loginEmail)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Timestamp -> String
     */
    private fun timestampToStr(time: Timestamp): String {
        return SimpleDateFormat("yyyy.MM.dd").format(time)
    }

    private fun stringToTimestamp(time: String): Timestamp {
        return Timestamp.valueOf(time)
    }

    /**
     * 조회수 문자열 제거
     */
    private fun strToIntViewCnt(viewCnt: String): Int {
        return viewCnt.replace("[^0-9]".toRegex(), "").toInt()
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

    private fun dynamicLayout2(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout2.visibility = View.VISIBLE
        binding.textViewTitle2.text = title
        binding.textViewContent2.text = content
        binding.textViewCreatedDate2.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt2.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate2.text = createdDate.toString()
        binding.buttonStatus2.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    private fun dynamicLayout3(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout3.visibility = View.VISIBLE
        binding.textViewTitle3.text = title
        binding.textViewContent3.text = content
        binding.textViewCreatedDate3.text = "작성일자 : " + timestampToStr(createdDate)
        binding.textViewViewCnt3.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate3.text = createdDate.toString()
        binding.buttonStatus3.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    private fun getLoginUserEmail(): String? {
        val sp: SharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        return sp.getString("loginEmail", null)
    }

    fun setUpButtonClick(button: Button, titleView: TextView, contentView: TextView) {
        button.setOnClickListener {
            AlertDialog.Builder(this).run {
                setTitle("안내")
                    .setMessage("모집 상태를 변경하시겠습니까?")
                    .setPositiveButton("완료") { dialog, which ->
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            val newStatus = if (button.text == "모집중") 0 else 1
                            val newButtonText = if (button.text == "모집중") "모집완료" else "모집중"
                            val newBackground =
                                if (button.text == "모집중") R.drawable.red_btn else R.drawable.green_btn
                            button.text = newButtonText
                            button.setBackgroundResource(newBackground)
                            updateBoardStatus(
                                titleView.text.toString().trim(),
                                contentView.text.toString().trim(),
                                newStatus
                            )
                        }
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }

    private fun updateBoardStatus(title: String, content: String, status: Int) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Boolean> = api.updateBoardStaus(BoardStatusDto(title, content), status)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(
                            applicationContext,
                            "모집 상태가 정상적으로 변경되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
            }
        })
    }

    private fun setLayoutClickListener(layout: View, titleView: TextView, contentView: TextView) {
        layout.setOnClickListener {
            val intent = Intent(this@EnrolBoardListActivity, ReadBoardActivity::class.java)
            intent.putExtra("title", titleView.text.toString())
            intent.putExtra("content", contentView.text.toString())
            startActivity(intent)
        }
    }
}