package com.example.ilink_beauty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var emailEt:EditText
    lateinit var passwordEt:EditText
    lateinit var loginBtn:Button

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEt = findViewById(R.id.email_et)
        passwordEt = findViewById(R.id.pwd_et)
        loginBtn = findViewById(R.id.button)

        // 자동 로그인 확인
        if (auth.currentUser != null) {
            navigateToMain()
        }


        loginBtn.setOnClickListener {
            var email = emailEt.text.toString()
            var password = passwordEt.text.toString()

            auth.createUserWithEmailAndPassword(email,password) // 회원 가입
                .addOnCompleteListener {
                        result ->
                    if(result.isSuccessful){
                        Toast.makeText(this,"회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        if(auth.currentUser!=null){
                            var intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    else if(result.exception?.message.isNullOrEmpty()){
                        Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        login(email,password)
                    }
                }
        }

    }


    fun login(email:String,password:String){
        auth.signInWithEmailAndPassword(email,password) // 로그인
            .addOnCompleteListener {
                    result->
                if(result.isSuccessful){
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    private fun navigateToMain() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 로그인 화면을 종료하여 뒤로 가기로 다시 돌아오면 로그인 화면이 나오지 않도록 함
    }

}