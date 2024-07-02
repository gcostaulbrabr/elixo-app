package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class DetalhesColetaActivity : AppCompatActivity() {
    private var coleta: Coleta? = null
    private var isPrestador: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_coleta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isPrestador = intent.getBooleanExtra("isPrestador", false)
        coleta = intent.getParcelableExtra("coleta")

        auth = Firebase.auth
        db = Firebase.firestore

        if (coleta != null) {
            initLayoutFields(coleta!!)
        }

        findViewById<Button>(R.id.btnDetalhesColetaCancelar).setOnClickListener {
            atualizarColeta(ColetaSituacao.CANCELADA)
        }

        findViewById<Button>(R.id.btnDetalhesColetaAtualizarSituacao).setOnClickListener {
            if (!isPrestador) {
                val rbAvaliacao = findViewById<RatingBar>(R.id.rbDetalhesColetaAvaliacao)
                if (rbAvaliacao.rating < 1 || rbAvaliacao.rating > 5) {
                    Toast.makeText(this, "Informe uma avaliação entre 1 e 5 estrelas", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val avaliacao = rbAvaliacao.rating.roundToInt()
                atualizarColeta(ColetaSituacao.AVALIADA, avaliacao)
            }
            else {
                val situacao: ColetaSituacao = if (coleta!!.status == ColetaSituacao.ACEITA.ordinal)
                    ColetaSituacao.COLETADA
                else
                    ColetaSituacao.ACEITA

                atualizarColeta(situacao)
            }
        }

        findViewById<Button>(R.id.btnDetalhesColetaVoltar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initLayoutFields(c: Coleta) {
        val tvDataHora = findViewById<TextView>(R.id.tvDetalhesColetaDataHora)
        val ivRelogioOff = findViewById<ImageView>(R.id.ivDetalhesColetaRelogioOff)
        val ivRelogioOn = findViewById<ImageView>(R.id.ivDetalhesColetaRelogioOn)
        val ivCaminhaoOff = findViewById<ImageView>(R.id.ivDetalhesColetaCaminhaoOff)
        val ivCaminhaoOn = findViewById<ImageView>(R.id.ivDetalhesColetaCaminhaoOn)
        val ivCheckOff = findViewById<ImageView>(R.id.ivDetalhesColetaCheckOff)
        val ivCheckOn = findViewById<ImageView>(R.id.ivDetalhesColetaCheckOn)
        val pbStatus = findViewById<ProgressBar>(R.id.pbDetalhesColetaStatus)
        val tvStatus = findViewById<TextView>(R.id.tvDetalhesColetaStatus)
        val ivFotoPrestador = findViewById<ImageView>(R.id.ivDetalhesColetaFotoPrestador)
        val tvNomePrestador = findViewById<TextView>(R.id.tvDetalhesColetaNomePrestador)
        val tvEndereco = findViewById<TextView>(R.id.tvDetalhesColetaEndereco)
        val tvAparelho = findViewById<TextView>(R.id.tvDetalhesColetaAparelho)
        val rbAvaliacao = findViewById<RatingBar>(R.id.rbDetalhesColetaAvaliacao)
        val btnCancelar = findViewById<Button>(R.id.btnDetalhesColetaCancelar)
        val btnAtualizarSituacao = findViewById<Button>(R.id.btnDetalhesColetaAtualizarSituacao)

        tvDataHora.text = DateTimeFormatter.ofPattern("'Coleta em' dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(c.dataHora)
        tvNomePrestador.text = c.prestadorNome
        tvEndereco.text = c.endereco
        tvAparelho.text = c.aparelhoTipo
        rbAvaliacao.rating = c.avaliacao.toFloat()

        // Atualiza imagens e botões
        // Inicializa com o texto mais comum para cada usuário, e depois muda quando precisar
        btnAtualizarSituacao.text = if (isPrestador) "Finalizar coleta" else "Avaliar coleta"
        // Esconde e desabilita tudo; mostra e habilita só quando precisar, baseado no status
        ivRelogioOff.visibility = ImageView.INVISIBLE
        ivRelogioOn.visibility = ImageView.INVISIBLE
        ivCaminhaoOff.visibility = ImageView.INVISIBLE
        ivCaminhaoOn.visibility = ImageView.INVISIBLE
        ivCheckOff.visibility = ImageView.INVISIBLE
        ivCheckOn.visibility = ImageView.INVISIBLE
        ivFotoPrestador.visibility = ImageView.INVISIBLE
        tvNomePrestador.visibility = TextView.INVISIBLE
        rbAvaliacao.visibility = View.INVISIBLE
        rbAvaliacao.isEnabled = false
        btnCancelar.isEnabled = false
        btnAtualizarSituacao.isEnabled = false

        when (c.status) {
            ColetaSituacao.SOLICITADA.ordinal -> {
                // Status 0:
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOff.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 15
                tvStatus.text = "Status: coleta solicitada"

                // Prestador não pode cancelar coleta que não aceitou ainda
                btnCancelar.isEnabled = !isPrestador
                // Para o prestador, botão Avaliar será usado para o aceite da solicitação de coleta
                btnAtualizarSituacao.isEnabled = isPrestador
                if (isPrestador) {
                    btnAtualizarSituacao.text = "Aceitar solicitação de coleta"
                }
            }
            ColetaSituacao.ACEITA.ordinal -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 60
                tvStatus.text = "Status: coleta aceita"
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE

                // Depois de aceita a coleta, somente o prestador pode cancelar ou finalizar
                btnCancelar.isEnabled = isPrestador
                btnAtualizarSituacao.isEnabled = isPrestador
            }
            ColetaSituacao.COLETADA.ordinal -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOn.visibility = ImageView.VISIBLE
                pbStatus.progress = 100
                tvStatus.text = "Status: coleta realizada${if (!isPrestador) ", avalie" else "" }!"
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE

                btnAtualizarSituacao.isEnabled = !isPrestador
                if (!isPrestador) {
                    rbAvaliacao.visibility = View.VISIBLE
                    rbAvaliacao.isEnabled = true
                }
            }
            ColetaSituacao.AVALIADA.ordinal -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOn.visibility = ImageView.VISIBLE
                pbStatus.progress = 100
                tvStatus.text = "Status: coleta encerrada e avaliada"
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE
                if (!isPrestador) {
                    rbAvaliacao.visibility = View.VISIBLE
                }
            }
            ColetaSituacao.CANCELADA.ordinal -> {
                ivRelogioOff.visibility = ImageView.VISIBLE
                ivCaminhaoOff.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 0
                tvStatus.text = "Status: coleta cancelada"
            }
            else -> {
                pbStatus.visibility = ProgressBar.INVISIBLE
                tvStatus.text = buildString {
                    append("STATUS INVÁLIDO! ")
                    append(c.status.toString())
                }
            }
        }
        // deixa os botões transparentes, suavizando a cor, caso tenham sido desabilitados
        if (!btnAtualizarSituacao.isEnabled) {
            btnAtualizarSituacao.alpha = 0.5f
        }
        if (!btnCancelar.isEnabled) {
            btnCancelar.alpha = 0.5f
        }
    }

    private fun atualizarColeta(situacao: ColetaSituacao, avaliacao: Int = 0) {
        val docRef = db.collection("coletas").document(coleta!!.id)
        val updates = hashMapOf<String, Any>(
            "status" to situacao.ordinal,
            "avaliacao" to avaliacao
        )

        docRef.update(updates)
            .addOnSuccessListener { _ ->
                Log.d("DetalhesColetaActivity", "Atualizada coleta ${coleta!!.id} para situação $situacao e avaliacao $avaliacao")
                Toast.makeText(baseContext, "Situação da coleta atualizada para ${situacao}!", Toast.LENGTH_SHORT,).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("NovaColetaActivity", "Atualização da coleta ${coleta!!.id} para situação $situacao e avaliacao $avaliacao falhou", e)
                Toast.makeText(baseContext, "Falha ao atualizar coleta: ${e.message}", Toast.LENGTH_LONG,).show()
            }
    }
}