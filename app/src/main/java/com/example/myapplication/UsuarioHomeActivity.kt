package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import java.time.ZonedDateTime

class UsuarioHomeActivity : AppCompatActivity() {
    private var isUsuarioPrestador: Boolean = false
    private var coletas: ArrayList<Coleta> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        internal const val CADASTRAR_COLETA = 0
        internal const val ALTERAR_COLETA = 1
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
        db = Firebase.firestore
        val usuarioNome = intent.getStringExtra("nomeUsuario")
        isUsuarioPrestador = intent.getBooleanExtra("isUsuarioPrestador", false)

        findViewById<ImageView>(R.id.ivUsuarioHomePrestador).visibility = if (isUsuarioPrestador) View.VISIBLE else View.INVISIBLE
        findViewById<TextView>(R.id.tvUsuarioHomeTitulo).text = "Olá $usuarioNome"
        findViewById<ProgressBar>(R.id.pbUsuarioHome).visibility = View.INVISIBLE

        setupRecyclerViewColetas()
        refreshColetas()
        Log.d("UsuarioHomeActivity::onCreate()", "Consulta retornou ${coletas.size} coletas")
        //refreshColetas()

        findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener {
            auth.signOut()
            onBackPressedDispatcher.onBackPressed()
        }
        val fabNovaColeta = findViewById<FloatingActionButton>(R.id.fabNovaColeta)
        if (isUsuarioPrestador) {
            // Prestador não adiciona solicitação de coleta
            fabNovaColeta.visibility = View.INVISIBLE
        }
        else {
            fabNovaColeta.setOnClickListener {
                val intent = Intent(this, NovaColetaActivity::class.java)
                startActivityForResult(intent, CADASTRAR_COLETA)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CADASTRAR_COLETA, ALTERAR_COLETA ->
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
            intent.putExtra("isPrestador", isUsuarioPrestador)
            startActivityForResult(intent, ALTERAR_COLETA)
        }
        adapter.onBindItem = { rlItem, coleta ->
            rlItem.background = when (coleta.status) {
                // Recém criada, ou já avaliada: borda preta, não precisa chamar atenção
                ColetaSituacao.SOLICITADA.ordinal, ColetaSituacao.AVALIADA.ordinal ->
                    ContextCompat.getDrawable(baseContext, R.drawable.background_border_inactive)
                // Aceita, ou concluída: borda verde, chama atenção pois coleta prosseguirá
                ColetaSituacao.ACEITA.ordinal, ColetaSituacao.COLETADA.ordinal ->
                    if (isUsuarioPrestador && coleta.status == ColetaSituacao.COLETADA.ordinal) {
                        // Para prestador, depois de COLETADA, já está finalizada, então não destaca
                        ContextCompat.getDrawable(baseContext, R.drawable.background_border_inactive)
                    }
                    else {
                        ContextCompat.getDrawable(baseContext, R.drawable.background_border_success)
                    }
                // Cancelada: borda vermelha, chama bastante atenção de que foi cancelada
                ColetaSituacao.CANCELADA.ordinal ->
                    ContextCompat.getDrawable(baseContext, R.drawable.background_border_error)
                else -> null
            }
        }
    }

    private fun refreshColetas() {
        val pb = findViewById<ProgressBar>(R.id.pbUsuarioHome)
        pb.visibility = View.VISIBLE
        //pb.setProgress(1, true)
        try {
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
            .orderBy("dataHora")
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
            // TODO: trazer coletas sem prestador somente se não estiverem canceladas
            .whereIn("prestadorId", mutableListOf(null, "", auth.currentUser?.uid))
            .orderBy("dataHora")
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
            doc.id,
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