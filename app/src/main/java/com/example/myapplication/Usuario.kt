package com.example.myapplication

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val prestador: Boolean = false,
    val prestadorTipoAparelho: String = "",
    val prestadorAreaAtuacao: String = ""
)
