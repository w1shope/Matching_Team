package com.example.matchingteam.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.R
import com.example.matchingteam.activity.board.BoardActivity
import com.example.matchingteam.activity.login.LoginActivity
import com.example.matchingteam.activity.board.EnrolBoardListActivity
import com.example.matchingteam.activity.myinfo.MyInfoActivity
import com.example.matchingteam.activity.register.RegisterActivity
import com.example.matchingteam.api.user.FindUserApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityHomeBinding
import com.example.matchingteam.dto.user.FindUserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeLoginStatus()
        binding.buttonLogin.setOnClickListener {
            val isLogin: Boolean = isLogin()
            // 로그인 상태 -> 로그아웃 처리
            if (isLogin) {
                logout()
                changeLoginStatus()
            }
            // 로그아웃 상태 -> 로그인 화면으로 이동
            else {
                val intent: Intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        binding.buttonRegister.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.buttonProject.setOnClickListener {
            val isLogin: Boolean = isLogin()
            // 팀 프로젝트 화면으로 이동
            if (isLogin) {
                val intent: Intent = Intent(this, BoardActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "로그인 후 사용가능합니다", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonMyinfo.setOnClickListener {
            val isLogin: Boolean = isLogin()
            // 사용자 정보 화면으로 이동
            if (isLogin) {
                val intent: Intent = Intent(this, MyInfoActivity::class.java)
                val sp: SharedPreferences =
                    getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
                val loginEmail = sp.getString("loginEmail", null)
                val loginPassword = sp.getString("loginPassword", null)
                intent.putExtra("loginEmail", loginEmail)
                intent.putExtra("loginPassword", loginPassword)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "로그인 후 사용가능합니다", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonDauHomepage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.donga.ac.kr"))
            startActivity(intent)
        }
        binding.buttonWrite.setOnClickListener {
            val isLogin: Boolean = isLogin()
            // 팀 프로젝트 화면으로 이동
            if (isLogin) {
                val intent: Intent = Intent(this, EnrolBoardListActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "로그인 후 사용가능합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 사용자가 등록한 게시물 개수, 댓글, 프로젝트 개수 표시
     */
    private fun updateUI() {
        val sp: SharedPreferences = getSharedPreferences()
        val email: String = sp.getString("loginEmail", null)!!
        val password: String = sp.getString("loginPassword", null)!!
        val retrofit = RetrofitConnection.getInstance()
        val api: FindUserApi = retrofit.create(FindUserApi::class.java)
        val call: Call<FindUserDto> = api.userInfo(email, password)
        call.enqueue(object : Callback<FindUserDto> {
            override fun onResponse(call: Call<FindUserDto>, response: Response<FindUserDto>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        binding.textViewWriteCount.text =
                            "작성한 게시물 : ${response.body()!!.writeCount.toString()}개"
                        val commentCount = response.body()?.commentCount ?: 0
                        val displayCount = if (commentCount == 0) {
                            "0"
                        } else {
                            (commentCount - 1).toString()
                        }
                        binding.textViewCommentCount.text = "작성한 댓글 : ${displayCount}개"
                    }
                }
            }

            override fun onFailure(call: Call<FindUserDto>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 로그인 여부에 따라 UI에 보여주는 내용(로그인<->로그아웃) 변경
     */
    private fun changeLoginStatus() {
        val sp: SharedPreferences = getSharedPreferences()
        // null : key에 대한 value가 없을 때, 반환할 값
        val loginEmail: String? = sp.getString("loginEmail", null)
        val loginPassword: String? = sp.getString("loginPassword", null)

        if (loginEmail != null && loginPassword != null) {
            val binding =
                ActivityHomeBinding.bind(findViewById(R.id.rootLayoutId)) // 기존의 ActivityMyInfoBinding 사용
            binding.buttonLogin.text = "로그아웃"
            updateUI()
        } else {
            val binding = ActivityHomeBinding.bind(findViewById(R.id.rootLayoutId))
            binding.buttonLogin.text = "로그인"
        }
    }

    /**
     * 로그아웃 메서드
     */
    private fun logout() {
        val sp: SharedPreferences = getSharedPreferences()
        val autoLoginEdit: SharedPreferences.Editor = sp.edit()
        autoLoginEdit.clear()
        autoLoginEdit.commit()
        Toast.makeText(applicationContext, "로그아웃이 완료되었습니다", Toast.LENGTH_SHORT).show()

        val intent: Intent = Intent(this@HomeActivity, HomeActivity::class.java)
        startActivity(intent)
    }

    /**
     * 로그인 상태인지 확인하는 메서드
     */
    private fun isLogin(): Boolean {
        val sp: SharedPreferences = getSharedPreferences()
        val loginEmail: String? = sp.getString("loginEmail", null)
        val loginPassword: String? = sp.getString("loginPassword", null)
        if (loginEmail == null || loginPassword == null)
            return false
        return true
    }

    private fun getSharedPreferences(): SharedPreferences {
        return getSharedPreferences("sharedPreferences", MODE_PRIVATE)
    }
}