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
            cadastrarUsuario(false)
        }

        findViewById<Button>(R.id.btn_registerPrestador).setOnClickListener {
            cadastrarUsuario(true)
        }
    }

    private fun cadastrarUsuario(isPrestador: Boolean) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val name = findViewById<EditText>(R.id.et_name).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val phone = findViewById<EditText>(R.id.et_phone).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        val deviceType = findViewById<EditText>(R.id.et_device_type).text.toString()
        val serviceArea = findViewById<EditText>(R.id.et_service_area).text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(auth.currentUser?.uid ?: "", name, email, phone)
                    db.collection("users").document(user.id).set(user)
                        .addOnSuccessListener {
                            if (isPrestador) {
                                val provider = Provider(
                                    user.id,
                                    user.name,
                                    user.email,
                                    user.phone,
                                    deviceType,
                                    serviceArea
                                )
                                db.collection("providers").document(user.id).set(provider)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Prestador registrado com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            "Falha no registro do prestador.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            else {
                                Toast.makeText(
                                    this,
                                    "Usuário registrado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Falha no registro do usuário.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
