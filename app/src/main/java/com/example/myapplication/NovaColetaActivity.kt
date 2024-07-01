package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.type.DateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class NovaColetaActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_coleta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        db = Firebase.firestore

        findViewById<Button>(R.id.btnAgendarColetaCriar).setOnClickListener {
            val tipoAparelho = findViewById<EditText>(R.id.etAgendarColetaTipoAparelho).text.toString()
            if (tipoAparelho.isEmpty()){
                Toast.makeText(baseContext, "Preencha o tipo de aparelho!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            val endereco = findViewById<EditText>(R.id.etAgendarColetaEndereco).text.toString()
            if (endereco.isEmpty()){
                Toast.makeText(baseContext, "Preencha o endereço!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            val coletaData = findViewById<EditText>(R.id.etAgendarColetaData).text.toString()
            if (coletaData.isEmpty()){
                Toast.makeText(baseContext, "Preencha a data!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            val coletaHora = findViewById<EditText>(R.id.etAgendarColetaHora).text.toString()
            if (coletaHora.isEmpty()){
                Toast.makeText(baseContext, "Preencha a hora!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            var dataColeta: LocalDate
            try {
                dataColeta = LocalDate.parse(coletaData, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
            catch (e: DateTimeParseException) {
                Toast.makeText(baseContext, "Preencha a data no formato dd/mm/aaaa!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            var horaColeta: LocalTime
            try {
                horaColeta = LocalTime.parse(coletaHora, DateTimeFormatter.ofPattern("HH:mm"))
            }
            catch (e: DateTimeParseException) {
                Toast.makeText(baseContext, "Preencha a hora no formato hh:mm!", Toast.LENGTH_SHORT,).show()
                return@setOnClickListener
            }
            val dataHoraColeta = ZonedDateTime.of(dataColeta, horaColeta, ZoneId.systemDefault())

            // Usa o hashmap abaixo no lugar de um objeto Coleta, porque o Firestore não serializa o
            // ZonedDateTime de dataHora para string ISO8601, e acaba gravando um objeto gigante de
            // data e hora. Serializa dataHora antes de gravar, e depois de ler (ex.: UsuarioHomeActivity::fetchColetasUsuario)
            val coleta = hashMapOf(
                "usuarioId" to auth.currentUser!!.uid,
                "prestadorId" to "",
                "prestadorNome" to "",
                "endereco" to endereco,
                "dataHora" to dataHoraColeta.format(DateTimeFormatter.ISO_DATE_TIME),
                "aparelhoTipo" to tipoAparelho,
                "status" to 0,
                "avaliacao" to 0
            )

            db.collection("coletas")
                .add(coleta)
                .addOnSuccessListener { docRef ->
                    Log.d("NovaColetaActivity", "Criada coleta ${docRef.id}")
                    Toast.makeText(baseContext, "Coleta criada com sucesso!", Toast.LENGTH_SHORT,).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("NovaColetaActivity", "Criação de coleta falhou", e)
                    Toast.makeText(baseContext, "Falha ao criar documento: ${e.message}", Toast.LENGTH_LONG,).show()
                }
        }
    }
}