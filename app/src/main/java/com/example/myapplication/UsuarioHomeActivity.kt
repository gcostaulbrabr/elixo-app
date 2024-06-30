package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.type.DateTime
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UsuarioHomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var coletas: ArrayList<Coleta>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.rvColetasUsuario)
        //recyclerView.setHasFixedSize()

        createMockColetas()
        val adapter = LinhaSolicitacaoColetaAdapter(coletas)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bgBorderSuccess =
            ContextCompat.getDrawable(baseContext, R.drawable.background_border_success)
        val bgBorderError =
            ContextCompat.getDrawable(baseContext, R.drawable.background_border_error)
        adapter.onItemClick = { coleta ->
            Toast.makeText(
                this,
                String.format(
                    "coleta em '%s' às '%s'",
                    coleta.endereco,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(coleta.dataHora)
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
        adapter.onBindItem = { rlItem, coleta ->
            rlItem.background = if (coleta.status == 0) bgBorderError else bgBorderSuccess
        }
    }
    
    private fun createMockColetas() {
        coletas = arrayListOf(
            Coleta(
                "1", "2", "", "Rua dos Bobos, 0 - POA/RS",
                ZonedDateTime.parse("2021-11-11T11:11:11.111-03:00"), "Geladeira",
                0
            ),
            Coleta(
                "2", "2", "3", "Rua do Limoeiro, 111 - Alvorada/RS",
                ZonedDateTime.parse("2024-02-03T07:45:13.768-03:00"), "Microondas",
                1
            ),
            Coleta("3", "2", "", "Praça do Avião, S/N - Canoas/RS",
                ZonedDateTime.parse("2012-12-21T23:58:59.123-02:00"), "Avião",
                0
            ),
            Coleta(
                "4", "2", "4", "Avenida Principal, 123/456 - POA/RS",
                ZonedDateTime.parse("2024-06-29T13:30:00.000-03:00"), "Lava loucas",
                1
            ),
            Coleta(
                "5", "2", "", "Avenida Principal, 123/321 - POA/RS",
                ZonedDateTime.parse("2024-06-29T14:00:00.000-03:00"), "Roupeiro",
                0
            ),
            Coleta(
                "6", "2", "3", "Avenida Principal, 123/456 - POA/RS",
                ZonedDateTime.parse("2024-06-30T09:30:00.000-03:00"), "Duas TV",
                1
            )
        )
    }
}