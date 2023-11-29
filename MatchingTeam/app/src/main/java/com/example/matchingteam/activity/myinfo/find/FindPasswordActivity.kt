package com.example.matchingteam.activity.myinfo.find

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.api.user.FindPasswordApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityFindPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFindPassword.setOnClickListener {
            val email = binding.editTextInputEmail.text.toString().trim()
            if (email.isEmpty())
                Toast.makeText(applicationContext, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            else {
                isExistEmail(email)
            }
        }
        binding.imageviewPrevPage.setOnClickListener {
            finish()
        }
        binding.imageViewExitPage.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 해당 이메일이 DB에 존재하는지 확인
     */
    private fun isExistEmail(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(FindPasswordApi::class.java)
        val call = api.existUser(email)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body() == true) {
                            val intent =
                                Intent(this@FindPasswordActivity, AuthenticateActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "해당 계정이 존재하지 않습니다",
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