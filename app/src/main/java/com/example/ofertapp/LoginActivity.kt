package com.example.ofertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ofertapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener{
            val email= binding.loginEmail.text.toString()
            val password= binding.loginPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent= Intent( this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Correo y/o Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        binding.singupRedirectText.setOnClickListener{
            val singUpIntent= Intent( this, SingUpActivity:: class.java)
            startActivity(singUpIntent)
        }
    }
}