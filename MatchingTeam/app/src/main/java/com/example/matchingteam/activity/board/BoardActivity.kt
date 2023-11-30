package com.example.matchingteam.activity.board

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.R
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityBoardBinding
import com.example.matchingteam.domain.board.Board
import com.example.matchingteam.dto.board.ListBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

class BoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boards()
        binding.buttonWrite.setOnClickListener {
            val intent: Intent = Intent(this@BoardActivity, EnrolBoardActivity::class.java)
            startActivity(intent)
        }
        binding.mainLayout1.setOnClickListener {
            val title = binding.textViewTitle1.text.toString().trim()
            val content = binding.textViewContent1.text.toString().trim()
            val createdDate = binding.hiddenCreatedDate1.text
            val viewCnt = strToIntViewCnt(binding.textViewViewCnt1.text.toString().trim())
            val sp: SharedPreferences =
                getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
            val loginEmail: String = sp.getString("loginEmail", null).toString().trim()
            findBoard(title, content, stringToTimestamp(createdDate.toString()), viewCnt)
        }

        binding.mainLayout2.setOnClickListener {
            val title = binding.textViewTitle2.text.toString().trim()
            val content = binding.textViewContent2.text.toString().trim()
            val createdDate = binding.hiddenCreatedDate2.text
            val viewCnt = strToIntViewCnt(binding.textViewViewCnt2.text.toString().trim())
            val sp: SharedPreferences =
                getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
            val loginEmail: String = sp.getString("loginEmail", null).toString().trim()
            findBoard(title, content, stringToTimestamp(createdDate.toString()), viewCnt)
        }

        binding.mainLayout3.setOnClickListener {
            val title = binding.textViewTitle3.text.toString().trim()
            val content = binding.textViewContent3.text.toString().trim()
            val createdDate = binding.hiddenCreatedDate3.text
            val viewCnt = strToIntViewCnt(binding.textViewViewCnt3.text.toString().trim())
            val sp: SharedPreferences =
                getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
            val loginEmail: String = sp.getString("loginEmail", null).toString().trim()
            findBoard(title, content, stringToTimestamp(createdDate.toString()), viewCnt)
        }

        binding.buttonBoardList.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.textViewShowAllBoard.setOnClickListener {
            if (binding.textViewTitle1.text.toString() != "출력할 제목")
                binding.mainLayout1.visibility = View.VISIBLE
            if (binding.textViewTitle2.text.toString() != "출력할 제목")
                binding.mainLayout2.visibility = View.VISIBLE
            if (binding.textViewTitle3.text.toString() != "출력할 제목")
                binding.mainLayout3.visibility = View.VISIBLE
            binding.textViewShowAllBoard.setTextColor(Color.BLACK)
            binding.textViewRecruitBoard.setTextColor(Color.GRAY)
            binding.textViewShowCompleteBoard.setTextColor(Color.GRAY)
        }
        binding.textViewRecruitBoard.setOnClickListener {
            if (binding.buttonStatus1.text == "모집중") {
                binding.mainLayout1.visibility = View.VISIBLE
            } else {
                binding.mainLayout1.visibility = View.GONE
            }
            if (binding.buttonStatus2.text == "모집중") {
                binding.mainLayout2.visibility = View.VISIBLE
            } else {
                binding.mainLayout2.visibility = View.GONE
            }
            if (binding.buttonStatus3.text == "모집중") {
                binding.mainLayout3.visibility = View.VISIBLE
            } else {
                binding.mainLayout3.visibility = View.GONE
            }
            binding.textViewShowAllBoard.setTextColor(Color.GRAY)
            binding.textViewRecruitBoard.setTextColor(Color.BLACK)
            binding.textViewShowCompleteBoard.setTextColor(Color.GRAY)
        }
        binding.textViewShowCompleteBoard.setOnClickListener {
            if (binding.buttonStatus1.text == "모집완료") {
                binding.mainLayout1.visibility = View.VISIBLE
            } else
                binding.mainLayout1.visibility = View.GONE
            if (binding.buttonStatus2.text == "모집완료") {
                binding.mainLayout2.visibility = View.VISIBLE
            } else
                binding.mainLayout2.visibility = View.GONE
            if (binding.buttonStatus3.text == "모집완료") {
                binding.mainLayout3.visibility = View.VISIBLE
            } else {
                binding.mainLayout3.visibility = View.GONE
            }
            binding.textViewShowAllBoard.setTextColor(Color.GRAY)
            binding.textViewRecruitBoard.setTextColor(Color.GRAY)
            binding.textViewShowCompleteBoard.setTextColor(Color.BLACK)
        }
    }

    /**
     * 모든 프로젝트 모집글 가져오기
     */
    private fun boards() {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<List<ListBoardDto>?> = api.boards()
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
                                        boards.get(index).name,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )

                                    1 -> dynamicLayout2(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).name,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )

                                    else -> dynamicLayout3(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).name,
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
    private fun findBoard(title: String, content: String, createdDate: Timestamp, viewCnt: Int) {
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
                        val intent = Intent(this@BoardActivity, ReadBoardActivity::class.java)
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
        return SimpleDateFormat("yyyy-MM-dd").format(time)
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
        name: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout1.visibility = View.VISIBLE
        binding.textViewTitle1.text = title
        binding.textViewContent1.text = content
        binding.textViewWriter1.text = "작성자 : ${name} "
        binding.textViewCreatedDate1.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt1.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate1.text = createdDate.toString()
        binding.buttonStatus1.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
        val newBackground =
            if (binding.buttonStatus1.text.toString()
                    .trim() == "모집완료"
            ) R.drawable.red_btn else R.drawable.green_btn
        binding.buttonStatus1.setBackgroundResource(newBackground)

    }

    private fun dynamicLayout2(
        title: String,
        content: String,
        name: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout2.visibility = View.VISIBLE
        binding.textViewTitle2.text = title
        binding.textViewContent2.text = content
        binding.textViewWriter2.text = "작성자 : ${name} "
        binding.textViewCreatedDate2.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt2.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate2.text = createdDate.toString()
        binding.buttonStatus2.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
        val newBackground =
            if (binding.buttonStatus2.text.toString()
                    .trim() == "모집완료"
            ) R.drawable.red_btn else R.drawable.green_btn
        binding.buttonStatus2.setBackgroundResource(newBackground)
    }

    private fun dynamicLayout3(
        title: String,
        content: String,
        name: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout3.visibility = View.VISIBLE
        binding.textViewTitle3.text = title
        binding.textViewContent3.text = content
        binding.textViewWriter3.text = "작성자 : ${name} "
        binding.textViewCreatedDate3.text = "작성일자 : " + timestampToStr(createdDate)
        binding.textViewViewCnt3.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate3.text = createdDate.toString()
        binding.buttonStatus3.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
        val newBackground =
            if (binding.buttonStatus3.text.toString()
                    .trim() == "모집완료"
            ) R.drawable.red_btn else R.drawable.green_btn
        binding.buttonStatus3.setBackgroundResource(newBackground)
    }
}
