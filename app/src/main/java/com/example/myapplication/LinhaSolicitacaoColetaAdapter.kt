package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Locale

class LinhaSolicitacaoColetaAdapter(private val coletas: ArrayList<Coleta>): RecyclerView.Adapter<LinhaSolicitacaoColetaAdapter.ViewHolder>() {
    var onItemClick: ((Coleta) -> Unit)? = null
    var onBindItem: ((RelativeLayout, Coleta) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.linha_solicitacao_coleta_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return coletas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = coletas[position]
        holder.tvEndereco.text = currentItem.endereco
        holder.tvDataHora.text = DateTimeFormatter.ofPattern("E dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(currentItem.dataHora)
        onBindItem?.invoke(holder.relativeLayout, currentItem)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvEndereco:TextView = itemView.findViewById(R.id.tvEnderecoColeta)
        val tvDataHora:TextView = itemView.findViewById(R.id.tvDataHoraColeta)
        val relativeLayout:RelativeLayout = tvEndereco.parent as RelativeLayout

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(coletas[adapterPosition])
            }
        }
    }
}