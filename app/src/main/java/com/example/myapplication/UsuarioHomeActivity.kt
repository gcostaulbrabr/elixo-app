package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.time.ZonedDateTime

class UsuarioHomeActivity : AppCompatActivity() {
    private var isUsuarioPrestador: Boolean = false
    private var coletas: ArrayList<Coleta> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth

    companion object {
        internal val CADASTRAR_COLETA = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        isUsuarioPrestador = intent.getBooleanExtra("isUsuarioPrestador", false)

        findViewById<ProgressBar>(R.id.pbUsuarioHome).visibility = View.INVISIBLE

        setupRecyclerViewColetas()
        refreshColetas()
        Log.d("UsuarioHomeActivity::onCreate()", "Consulta retornou ${coletas.size} coletas")
        //refreshColetas()

        findViewById<FloatingActionButton>(R.id.fabNovaColeta).setOnClickListener {
            val intent = Intent(this, NovaColetaActivity::class.java)
            startActivityForResult(intent, CADASTRAR_COLETA)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CADASTRAR_COLETA ->
                if (resultCode == RESULT_OK)
                    refreshColetas()
        }
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

    private fun refreshColetas() {
        val pb = findViewById<ProgressBar>(R.id.pbUsuarioHome)
        pb.visibility = View.VISIBLE
        //pb.setProgress(1, true)
        try {
            val db = Firebase.firestore
            coletas.clear()
            if (isUsuarioPrestador) {
                fetchColetasPrestador(db)
            }
            else {
                fetchColetasUsuario(db)
            }
        }
        catch (e: Exception) {
            Log.e("UsuarioHomeActivity::refreshColetas()", "Falhou ao atualizar coletas", e)
            Toast.makeText(this, "Erro ao atualizar lista de coletas: ${e.message}", Toast.LENGTH_LONG).show()
        }
        finally {
            pb.visibility = View.INVISIBLE
        }
    }

    private fun fetchColetasUsuario(db: FirebaseFirestore) {
        db.collection("coletas")
            .whereEqualTo("usuarioId", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val coleta = querydocumentToColeta(doc!!)
                    coletas.add(coleta)
                }
                Log.d("UsuarioHomeActivity::fetchColetasUsuario()", "Buscou ${coletas.size} coletas")
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("UsuarioHomeActivity::fetchColetasUsuario()", "Falhou ao buscar coletas", e)
                Toast.makeText(this, "Erro ao buscar coletas do usuário: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchColetasPrestador(db: FirebaseFirestore) {
        db.collection("coletas")
            .where(
                // Busca solicitações de coleta já atribuídas a si, e as sem prestador (disponíveis)
                Filter.or(
                    Filter.equalTo("prestadorId", auth.currentUser?.uid),
                    Filter.equalTo("prestadorId", null)
                )
            )
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val coleta = querydocumentToColeta(doc!!)
                    coletas.add(coleta)
                }
                Log.d("UsuarioHomeActivity::fetchColetasPrestador()", "Buscou ${coletas.size} coletas")
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("UsuarioHomeActivity::fetchColetasPrestador()", "Falhou ao buscar coletas", e)
                Toast.makeText(this, "Erro ao buscar coletas do prestador: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun querydocumentToColeta(doc: QueryDocumentSnapshot): Coleta {
        return Coleta(
            doc["id"].toString(),
            doc["usuarioId"].toString(),
            doc["prestadorId"].toString(),
            doc["prestadorNome"].toString(),
            doc["endereco"].toString(),
            ZonedDateTime.parse(doc["dataHora"].toString()),
            doc["aparelhoTipo"].toString(),
            doc["status"].toString().toInt(),
            doc["avaliacao"].toString().toInt()
        )
    }
}