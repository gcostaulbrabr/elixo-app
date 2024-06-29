package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class LinhaSolicitacaoColetaAdapter(private val coletas: ArrayList<Coleta>): RecyclerView.Adapter<LinhaSolicitacaoColetaAdapter.ViewHolder>() {
    var onItemClick: ((Coleta) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.linha_solicitacao_coleta_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return coletas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = coletas[position]
        holder.rvEndereco.text = currentItem.endereco
        holder.rvDataHora.text = currentItem.dataHora.toString()
    }
    
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvEndereco:TextView = itemView.findViewById(R.id.tvEnderecoColeta)
        val rvDataHora:TextView = itemView.findViewById(R.id.tvDataHoraColeta)
        
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(coletas[adapterPosition])
            }
        }
    }
}