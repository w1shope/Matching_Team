package com.example.matchingteam.activity.myinfo.find

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.login.LoginActivity
import com.example.matchingteam.api.user.FindPasswordApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityShowPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        showPassword(intent.getStringExtra("email")!!)

        binding.buttonMoveLogin.setOnClickListener {
            val intent = Intent(this@ShowPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showPassword(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(FindPasswordApi::class.java)
        val call = api.getUserPassword(email)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        binding.textViewPassword.text = response.body()!!
                    }
                } else {
                    Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}