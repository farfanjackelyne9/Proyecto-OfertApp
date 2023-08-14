package com.example.ofertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ofertapp.databinding.ActivityLoginBinding
import com.example.ofertapp.databinding.ActivitySingUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()

        binding.sinupButton.setOnClickListener{

            val email= binding.singupEmail.text.toString()
            val password=  binding.singupPassword.text.toString()
            val confirmPassword= binding.singupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() ){
                if(password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent= Intent(this, LoginActivity:: class.java)
                            startActivity(intent)
                        }else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

                }
            } else{
                Toast.makeText(this, "complete todos los campos", Toast.LENGTH_SHORT).show()

            }
        }
        binding.loginRedirectText.setOnClickListener{
            val loginIntent= Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}