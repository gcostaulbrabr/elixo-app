package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.ZonedDateTime

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

        createMockColetas()
        sortColetas()
        setupRecyclerViewColetas()
    }

    private fun createMockColetas() {
        coletas = arrayListOf(
            Coleta(
                "1", "2", "", "",
                "Rua dos Bobos, 0 - POA/RS",
                ZonedDateTime.parse("2021-11-11T11:11:11.111-03:00"),
                "Geladeira", 3
            ),
            Coleta(
                "2", "2", "3", "Dejair Soares",
                "Rua do Limoeiro, 111 - Alvorada/RS",
                ZonedDateTime.parse("2024-02-03T07:45:13.768-03:00"),
                "Microondas", 1
            ),
            Coleta("3", "2", "", "",
                "Praça do Avião, S/N - Canoas/RS",
                ZonedDateTime.parse("2012-12-21T23:58:59.123-02:00"),
                "Avião", 0
            ),
            Coleta(
                "4", "2", "4", "Katia Pereira",
                "Avenida Principal, 123/456 - POA/RS",
                ZonedDateTime.parse("2024-06-29T13:30:00.000-03:00"),
                "Lava loucas", 2
            ),
            Coleta(
                "4", "2", "4", "Katia Pereira",
                "Avenida Principal, 123/222 - POA/RS",
                ZonedDateTime.parse("2024-06-29T15:30:00.000-03:00"),
                "Freezer horizontal GIGANTE de duas portas ENORME", 4, 5
            ),
            Coleta(
                "5", "2", "", "",
                "Avenida Principal, 123/321 - POA/RS",
                ZonedDateTime.parse("2024-06-29T14:00:00.000-03:00"), "Roupeiro",
                0
            ),
            Coleta(
                "6", "2", "3", "Dejair Soares",
                "Avenida Principal, 123/456 - POA/RS",
                ZonedDateTime.parse("2024-06-30T09:30:00.000-03:00"), "Duas TV",
                3
            )
        )
    }

    private fun sortColetas() {
        coletas = ((coletas.sortedWith(compareBy { it.dataHora })).toCollection(ArrayList()))
    }

    private fun setupRecyclerViewColetas() {
        recyclerView = findViewById(R.id.rvColetasUsuario)
        //recyclerView.setHasFixedSize()

        val adapter = LinhaSolicitacaoColetaAdapter(coletas)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val parentScope = this
        adapter.onItemClick = { coleta ->
            val intent = Intent(parentScope, DetalhesColetaActivity::class.java)
            intent.putExtra("coleta", coleta)
            parentScope.startActivity(intent)
        }
        adapter.onBindItem = { rlItem, coleta ->
            // status:
            // 0=aguardando prestador
            // 1=prestador aceitou, aguardando coleta
            // 2=coleta concluída, aguardando avaliação
            // 3=coleta cancelada
            // 4=coleta concluída e avaliada
            rlItem.background = when (coleta.status) {
                0, 4 -> ContextCompat.getDrawable(baseContext, R.drawable.background_border_inactive)
                1, 2 -> ContextCompat.getDrawable(baseContext, R.drawable.background_border_success)
                3 -> ContextCompat.getDrawable(baseContext, R.drawable.background_border_error)
                else -> null
            }
        }
    }
}