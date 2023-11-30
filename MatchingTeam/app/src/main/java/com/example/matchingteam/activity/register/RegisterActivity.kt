package com.example.matchingteam.activity.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchingteam.R
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.api.user.RegisterUserApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityRegisterBinding
import com.example.matchingteam.dto.user.RegisterUserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    val development: Array<String> = arrayOf("백엔드", "프론트엔드", "데이터분석", "프로젝트 기획", "디자인")
    var department: Array<String> = arrayOf(
        "한국어문학과", "영어영문학과", "고고미술사학과", "교육학과", "경제학과", "미디어커뮤니케이션학과", "경영학과", "무역학과", "회계학과",
        "관광경영학과", "경영정보학과", "수학과", "화학과", "식품영양학과", "의상섬유학과", "식품생명공학과", "응용생명과학과", "건강과학과",
        "중개의과학과", "건축학과", "토목공학과", "기계공학과", "화학공학과", "전기공학과", "환경공학과", "금속공학과", "전자공학과", "조경학과",
        "도시계획학과", "조선해양플랜트공학과", "체육학과", "태권도학과", "미술학과", "음악학과", "조형디자인학과", "의학과", "국제법무학과", "예술학과",
        "기업정책학과", "스포츠의학과", "ICT 융합해양스마트시티공학과", "컴퓨터공학과", "AI공학과"
    )
    var authenticateCode: String? = null // 학교 인증 코드
    var isSuccessAuthentiate: Boolean = false
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 학교 인증 버튼
        binding.buttonRegisterCheckEmailBtn.setOnClickListener {
            val email: String = binding.editTextRegisterEmail.text.toString()
            val emailPattern = Regex(".*@donga\\.ac\\.kr$")
            val isMatch = emailPattern.matches(email)
            // "@donga.ac.kr"로 끝날 때
            if (isMatch) {
                /**
                 * 스프링 서버에서 인증 코드를 생성한다.
                 * 현재 입력된 이메일로 인증 코드를 전송한다.
                 * 안드로이드 스튜디오에서 해당 인증코드가 일치하는지 확인한다.
                 * 인증이 완료되면 "학교 인증" -> "인증 완료"로 수정한다.
                 */
                existUserEmail(email)
            }
            // "@donga.ac.kr"로 끝나지 않을 때
            else {
                Toast.makeText(applicationContext, "동아대학교 이메일이 아닙니다", Toast.LENGTH_SHORT).show()
                binding.editTextRegisterEmail.setText("")
            }
        }

        // 개발 희망 분야 선택
        val developmentSpinner = binding.spinnerDevelopment
        val developmentAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, development)
        developmentAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        developmentSpinner.setAdapter(developmentAdapter)

        var developmentSelectedItem = "백엔드"
        developmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                developmentSelectedItem = development[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 학과 선택
        val departmentSpinner = binding.spinnerDepartmentDropDown
        val departmentAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, department)
        departmentAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        departmentSpinner.setAdapter(departmentAdapter)

        var departmentSelectedItem = "한국어문학과"
        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                departmentSelectedItem = department[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.buttonMoveMain.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        val registerBtn: Button = binding.buttonRegisterUser
        registerBtn.setOnClickListener {
            if (isSuccessAuthentiate) {
                val email: String = binding.editTextRegisterEmail.text.toString()
                val name: String = binding.editTextRegisterName.text.toString()
                val password: String = binding.editTextRegisterPassword.text.toString()
                val studentNum: Int = binding.editTextRegisterStudentNumber.text.toString().toInt()
                val department: String = departmentSelectedItem
                val development: String = developmentSelectedItem
                createUser(email, name, password, studentNum, department, development)
            } else {
                Toast.makeText(applicationContext, "학교 인증이 완료되지 않았습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 계정 생성 로직
     */
    private fun createUser(
        email: String,
        name: String,
        password: String,
        studentNum: Int,
        department: String,
        development: String
    ) {
        val retrofit = RetrofitConnection.getInstance()
        val api: RegisterUserApi = retrofit.create(RegisterUserApi::class.java)
        val call: Call<Long> =
            api.saveUser(
                RegisterUserDto(
                    email,
                    name,
                    password,
                    studentNum,
                    department,
                    development
                )
            )
        call.enqueue(object : Callback<Long> {
            override fun onResponse(
                call: Call<Long>,
                response: Response<Long>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(applicationContext, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 1500)
                    }
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 이메일 인증 로직
     */
    private fun checkUserEmail(address: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: RegisterUserApi = retrofit.create(RegisterUserApi::class.java)
        val call: Call<String> =
            api.userEmailAuthentication(address)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(
                            applicationContext,
                            "입력하신 메일로 인증코드가 전송되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        authenticateCode = response.body().toString()
                    } else {
                        Toast.makeText(applicationContext, "잠시후 다시 시도해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun checkUserEmailConfirm(authenticateCode: Int) {
        val retrofit = RetrofitConnection.getInstance()
        val api: RegisterUserApi = retrofit.create(RegisterUserApi::class.java)
        val call: Call<Boolean> = api.userEmailAuthenticationConfirm(authenticateCode)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        isSuccessAuthentiate = response.body()!!
                        if (isSuccessAuthentiate) {
                            Toast.makeText(
                                applicationContext,
                                "학생 인증이 완료 되었습니다",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.buttonRegisterCheckEmailBtn.text = "인증 완료"
                            modifyPrevention()
                            binding.buttonRegisterCheckEmailConfirmBtn.visibility = View.GONE
                            binding.editTextRegisterCheckEmailConfirm.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "인증 코드가 일치하지 않습니다",
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

    /**
     * 학교 인증 완료 후 email 수정 방지
     */
    private fun modifyPrevention() {
        binding.editTextRegisterEmail.isClickable = false
        binding.editTextRegisterEmail.isFocusable = false
        binding.editTextRegisterEmail.isFocusableInTouchMode = false
        binding.editTextRegisterEmail.setTextColor(Color.GRAY)
    }

    /**
     * 이메일 중복 체크
     */
    private fun existUserEmail(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api = retrofit.create(RegisterUserApi::class.java)
        val call = api.existUserEmail(email)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body() == true) {
                            Toast.makeText(applicationContext, "이미 존재하는 계정입니다", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            checkUserEmail(email)
                            binding.editTextRegisterCheckEmailConfirm.visibility = View.VISIBLE
                            binding.buttonRegisterCheckEmailConfirmBtn.visibility = View.VISIBLE
                            binding.buttonRegisterCheckEmailBtn.setBackgroundResource(R.drawable.send_ok_button)
                            binding.buttonRegisterCheckEmailBtn.text = "인증 코드 재전송"
                            binding.buttonRegisterCheckEmailConfirmBtn.setOnClickListener {
                                checkUserEmailConfirm(
                                    binding.editTextRegisterCheckEmailConfirm.text.toString()
                                        .toInt()
                                )
                            }
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