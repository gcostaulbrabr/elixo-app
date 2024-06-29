package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.type.DateTime
import java.time.LocalDateTime

class UsuarioHomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Coleta>

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

        //LocalDateTime.of(2012, 12, 21, 13, 15 ,15)
        val dt1 = DateTime.newBuilder().setYear(2012).setMonth(12).setDay(21).setHours(13).setMinutes(14).setSeconds(15).build()
        val dt2 = DateTime.newBuilder().setYear(2012).setMonth(12).setDay(21).setHours(17).setMinutes(15).setSeconds(13).build()
        val dt3 = DateTime.newBuilder().setYear(2012).setMonth(12).setDay(21).setHours(23).setMinutes(58).setSeconds(59).build()
        dataList = arrayListOf(
            Coleta("1", "2", "", "Rua dos Bobos, 0 - POA/RS", dt1, "Geladeira", 0),
            Coleta("2", "2", "3", "Rua do Limoeiro, 111 - Alvorada/RS", dt2, "Microondas", 1),
            Coleta("3", "2", "", "Praça do Avião, S/N - Canoas/RS", dt3, "Avião", 0)
        )

        val adapter = LinhaSolicitacaoColetaAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.onItemClick = {
            coleta -> Toast.makeText(this, String.format("coleta em '%s' às '%s'", coleta.endereco, coleta.dataHora.toString()), Toast.LENGTH_SHORT).show()
        }
    }
}