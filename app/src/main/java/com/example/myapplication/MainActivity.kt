package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth
        db = Firebase.firestore

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(baseContext, "Preencha o email!", Toast.LENGTH_SHORT,).show()
                Log.e("MainActivity::onCreate", "Email vazio")
                return@setOnClickListener
            }
            if (senha.isEmpty()) {
                Toast.makeText(baseContext, "Preencha a senha!", Toast.LENGTH_SHORT,).show()
                Log.e("MainActivity::onCreate", "Senha vazia")
                return@setOnClickListener
            }

            login(email, senha)
        }

        findViewById<Button>(R.id.btnEsqueciSenha).setOnClickListener {
            Toast.makeText(this, "Que pena :-(  (TBD)", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnCadastrar).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Somente para facilitar os testes
        findViewById<Button>(R.id.btnLoginUsuarioTeste).setOnClickListener {
            login("foo@bar.net", "FooBar123!")
        }
        findViewById<Button>(R.id.btnLoginPrestadorTeste).setOnClickListener {
            login("bar@foo.net", "BarFoo321!")
        }
    }

    private fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Autenticação falhou.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.e("MainActivity::onCreate", "Autenticação falhou")
                    return@addOnCompleteListener
                }

                // Login falhou
                if (auth.currentUser == null) {
                    Log.e("MainActivity::onCreate", "Nenhum usuário logado")
                    return@addOnCompleteListener
                }

                db.collection("usuarios")
                    .document(auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { docRef ->
                        val nome = docRef["nome"].toString()
                        val isPrestador = docRef["prestador"].toString().toBoolean()

                        val intent = Intent(this, UsuarioHomeActivity::class.java)
                        intent.putExtra("nomeUsuario", nome)
                        intent.putExtra("isUsuarioPrestador", isPrestador)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MainActivity::onCreate", "Não localizou usuário", e)
                    }
            }
    }
}