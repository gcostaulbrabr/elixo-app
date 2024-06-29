package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()
            // TODO: login com firebase
//            if (email != "foo@bar.net" || senha != "foobar") {
//                Toast.makeText(this, "Login errado :-(", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val isPrestador = false
            if (isPrestador) {
                // TODO: criar PrestadorHomeActivity
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            else {
                startActivity(Intent(this, UsuarioHomeActivity::class.java))
            }
        }

        findViewById<Button>(R.id.btnEsqueciSenha).setOnClickListener {
            Toast.makeText(this, "Que pena :-(", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnCadastrar).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}