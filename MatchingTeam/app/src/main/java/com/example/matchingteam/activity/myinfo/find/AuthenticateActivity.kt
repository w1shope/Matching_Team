package com.example.matchingteam.activity.myinfo.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.api.user.FindPasswordApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityAuthenticateBinding
import com.example.matchingteam.dto.user.UserEmailAuthenticateDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticateActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthenticateBinding
    lateinit var authenticateDto: UserEmailAuthenticateDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        val email = intent.getStringExtra("email")!!
        findPassword(email)
        binding.buttonAuthenticateCode.setOnClickListener {
            if (binding.editTextAuthenticateCode.text.toString().trim().isEmpty())
                Toast.makeText(applicationContext, "인증코드를 입력해주세요", Toast.LENGTH_SHORT).show()
            else
                authenticate(binding.editTextAuthenticateCode.text.toString().toInt(), email)
        }
        binding.imageviewPrevPage.setOnClickListener {
            finish()
        }
        binding.imageViewExitPage.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.imageviewPrevPage.setOnClickListener {
            finish()
        }
    }

    /**
     * 비밀번호 찾기 API
     */
    private fun findPassword(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(FindPasswordApi::class.java)
        val call = api.findPassword(email)
        call.enqueue(object : Callback<UserEmailAuthenticateDto> {
            override fun onResponse(
                call: Call<UserEmailAuthenticateDto>,
                response: Response<UserEmailAuthenticateDto>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(applicationContext, "인증 코드가 전송되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        authenticateDto = response.body()!!
                    }
                }
            }

            override fun onFailure(call: Call<UserEmailAuthenticateDto>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 인증 코드 일치 여부 API
     */
    private fun authenticate(authenticateCode: Int, email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(FindPasswordApi::class.java)
        val call = api.authenticate(authenticateCode)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body()!!) {
                        if (response.body() == true) {
                            // 비밀번호 표시 페이지로 이동
                            val intent =
                                Intent(this@AuthenticateActivity, ShowPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "인증코드가 일치하지 않습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}