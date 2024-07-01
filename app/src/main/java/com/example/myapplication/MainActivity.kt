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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()
            if (email.isEmpty()) {
                Toast.makeText(baseContext, "Preencha o email!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            if (senha.isEmpty()) {
                Toast.makeText(baseContext, "Preencha a senha!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Autenticação falhou.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

            // Login falhou
            if (auth.currentUser == null) {
                return@setOnClickListener
            }

            val intent = Intent(this, UsuarioHomeActivity::class.java)
            intent.putExtra("isUsuarioPrestador", false)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnEsqueciSenha).setOnClickListener {
            Toast.makeText(this, "Que pena :-(  (TBD)", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnCadastrar).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}