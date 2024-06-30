package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetalhesColetaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_coleta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnDetalhesColetaCancelar).setOnClickListener {
            // TODO: atualizar situação da coleta
            Toast.makeText(this, "Coleta cancelada", Toast.LENGTH_LONG).show()
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<Button>(R.id.btnDetalhesColetaAvaliar).setOnClickListener {
            // TODO: abrir modal para avaliar
            // TODO: atualizar situação da coleta
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<Button>(R.id.btnDetalhesColetaVoltar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val coleta = intent.getParcelableExtra<Coleta>("coleta")
        if (coleta != null) {
            initLayoutFields(coleta)
        }
    }

    private fun initLayoutFields(coleta: Coleta) {
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
        val btnCancelar = findViewById<Button>(R.id.btnDetalhesColetaCancelar)
        val btnAvaliar = findViewById<Button>(R.id.btnDetalhesColetaAvaliar)

        tvDataHora.text = DateTimeFormatter.ofPattern("'Coleta em' dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(coleta.dataHora)
        tvNomePrestador.text = coleta.prestadorNome
        tvEndereco.text = coleta.endereco
        tvAparelho.text = coleta.aparelhoTipo

        // Atualiza imagens e botões
        // Esconde e desabilita tudo; mostra e habilita só quando precisar, baseado no status
        ivRelogioOff.visibility = ImageView.INVISIBLE
        ivRelogioOn.visibility = ImageView.INVISIBLE
        ivCaminhaoOff.visibility = ImageView.INVISIBLE
        ivCaminhaoOn.visibility = ImageView.INVISIBLE
        ivCheckOff.visibility = ImageView.INVISIBLE
        ivCheckOn.visibility = ImageView.INVISIBLE
        ivFotoPrestador.visibility = ImageView.INVISIBLE
        tvNomePrestador.visibility = TextView.INVISIBLE
        btnCancelar.isEnabled = false
        btnAvaliar.isEnabled = false
        when (coleta.status) {
            0 -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOff.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 15
                tvStatus.text = "Status: coleta solicitada"
                btnCancelar.isEnabled = true
            }
            1 -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 60
                tvStatus.text = "Status: coleta aceita"
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE
                btnCancelar.isEnabled = true
            }
            2 -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOn.visibility = ImageView.VISIBLE
                pbStatus.progress = 100
                tvStatus.text = "Status: coleta concluída, avalie!"
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE
                btnAvaliar.isEnabled = true
            }
            3 -> {
                ivRelogioOff.visibility = ImageView.VISIBLE
                ivCaminhaoOff.visibility = ImageView.VISIBLE
                ivCheckOff.visibility = ImageView.VISIBLE
                pbStatus.progress = 0
                tvStatus.text = "Status: coleta cancelada"
            }
            4 -> {
                ivRelogioOn.visibility = ImageView.VISIBLE
                ivCaminhaoOn.visibility = ImageView.VISIBLE
                ivCheckOn.visibility = ImageView.VISIBLE
                pbStatus.progress = 100
                tvStatus.text = buildString {
                    append("Status: coleta concluída, nota ")
                    append(coleta.avaliacao.toString())
                    append("/5")
                }
                ivFotoPrestador.visibility = ImageView.VISIBLE
                tvNomePrestador.visibility = TextView.VISIBLE
            }
            else -> {
                pbStatus.visibility = ProgressBar.INVISIBLE
                tvStatus.text = buildString {
                    append("STATUS INVÁLIDO! ")
                    append(coleta.status.toString())
                }
            }
        }
        // deixa o botão de cancelar transparente caso tenha sido desabilitado, suavizando a cor
        if (!btnCancelar.isEnabled) {
            btnCancelar.alpha = 0.5f
        }
    }
}