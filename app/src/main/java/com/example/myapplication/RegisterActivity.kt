package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.btn_registerUsuario).setOnClickListener {
            if (cadastrarUsuario(false)) {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        findViewById<Button>(R.id.btn_registerPrestador).setOnClickListener {
            if (cadastrarUsuario(true)) {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        findViewById<Button>(R.id.btnRegistrarCancelar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun cadastrarUsuario(isPrestador: Boolean): Boolean {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val nome = findViewById<EditText>(R.id.et_name).text.toString()
        if (nome.isEmpty()) {
            Toast.makeText(baseContext, "Preencha o nome!", Toast.LENGTH_SHORT,).show()
            return false
        }
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        if (email.isEmpty()) {
            Toast.makeText(baseContext, "Preencha o email!", Toast.LENGTH_SHORT,).show()
            return false
        }
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        if (password.isEmpty()) {
            Toast.makeText(baseContext, "Preencha a senha!", Toast.LENGTH_SHORT,).show()
            return false
        }
        val fone = findViewById<EditText>(R.id.et_phone).text.toString()
        if (fone.isEmpty()) {
            Toast.makeText(baseContext, "Preencha o fone!", Toast.LENGTH_SHORT,).show()
            return false
        }

        val tipoAparelho = findViewById<EditText>(R.id.et_device_type).text.toString()
        val areaAtuacao = findViewById<EditText>(R.id.et_service_area).text.toString()
        if (isPrestador) {
            if (tipoAparelho.isEmpty()) {
                Toast.makeText(baseContext, "Preencha o tipo de aparelho!", Toast.LENGTH_SHORT,).show()
                return false
            }
            if (areaAtuacao.isEmpty()) {
                Toast.makeText(baseContext, "Preencha a área de atuação!", Toast.LENGTH_SHORT,).show()
                return false
            }
        }

        var result = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = Usuario(
                        auth.currentUser?.uid ?: "", nome, email, fone, isPrestador,
                        if (isPrestador) tipoAparelho else "",
                        if (isPrestador) areaAtuacao else ""
                    )

                    db.collection("usuarios").document(user.id).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "${if (isPrestador) "Prestador" else "Usuário"} registrado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()

                            result = true
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Falha no registro do usuário.", Toast.LENGTH_SHORT).show()
                            result = false
                        }
                } else {
                    Toast.makeText(this, "Falha na criação do usuário.", Toast.LENGTH_SHORT).show()
                    result = false
                }
            }

        return result
    }
}
