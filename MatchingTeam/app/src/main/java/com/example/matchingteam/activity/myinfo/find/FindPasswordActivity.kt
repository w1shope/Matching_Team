package com.example.matchingteam.activity.myinfo.find

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.databinding.ActivityFindPasswordBinding

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
                val intent = Intent(this, AuthenticateActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
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
}