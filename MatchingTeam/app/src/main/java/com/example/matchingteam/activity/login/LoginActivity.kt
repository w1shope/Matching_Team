package com.example.matchingteam.activity.login

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.activity.myinfo.find.FindPasswordActivity
import com.example.matchingteam.activity.register.RegisterActivity
import com.example.matchingteam.api.user.LoginUserApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityLoginBinding
import com.example.matchingteam.dto.user.LoginUserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 자동 로그인
        autoLogin()

        binding.buttonLoginUser.setOnClickListener {
            val email: String = binding.editTextLoginEmail.text.toString().trim()
            val password: String = binding.editTextLoginPassword.text.toString().trim()
            checkLogin(email, password)
        }
        binding.checkboxAutoLogin.setOnClickListener {
            val email: String = binding.editTextLoginEmail.text.toString().trim()
            val password: String = binding.editTextLoginPassword.text.toString().trim()
            isClickAutoLoginCheckBox(binding.checkboxAutoLogin, email, password)
        }
        binding.textViewRegisterBtn.setOnClickListener {
            val intent: Intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLogin(email: String, password: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: LoginUserApi = retrofit.create(LoginUserApi::class.java)
        val call: Call<LoginUserDto> = api.findByUserEmailAndPassword(email, password)
        call.enqueue(object : Callback<LoginUserDto> {
            override fun onResponse(call: Call<LoginUserDto>, response: Response<LoginUserDto>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(applicationContext, "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show()
                        // Toast 메시지를 띄우고 1.5초 후에 finish() 호출한다.
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent: Intent =
                                Intent(this@LoginActivity, HomeActivity::class.java)
                            login(email, password)
                            startActivity(intent)
                        }, 750)
                    }
                } else {
                    Toast.makeText(applicationContext, "아이디 또는 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<LoginUserDto>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 로그인 시에 자동 로그인을 클릭했는지 확인하는 메서드
     */
    private fun isClickAutoLoginCheckBox(checkBox: CheckBox, email: String, password: String) {
        // 자동로그인 클릭하였을 때
        if (checkBox.isChecked) {
            val auto: SharedPreferences =
                getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
            val autoLoginEdit: SharedPreferences.Editor = auto.edit()
            // 로그인 정보 저장
            autoLoginEdit.putString("autoLoginEmail", email)
            autoLoginEdit.putString("autoLoginPassword", password)
            autoLoginEdit.commit()
        }
    }

    /**
     * 자동 로그인 메서드
     */
    private fun autoLogin() {
        val sp: SharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
        // null : key에 대한 value가 없을 때, 반환할 값
        val autoLoginEmail: String? = sp.getString("autoLoginEmail", null)
        val autoLoginPassword: String? = sp.getString("autoLoginPassword", null)

        if (autoLoginEmail != null && autoLoginPassword != null) {
            checkLogin(autoLoginEmail, autoLoginPassword)
        }
    }

    /**
     * 로그인 정보 저장
     */
    private fun login(email: String, password: String) {
        val sp: SharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.putString("loginEmail", email)
        edit.putString("loginPassword", password)
        edit.commit()
    }
}