package com.example.myapplication

import com.google.type.DateTime

data class Coleta(
    val id: String = "",
    val usuarioId: String = "",
    val prestadorId: String = "",
    val endereco: String = "",
    val dataHora: DateTime,
    val aparelhoTipo: String = "",
    val status: Int // TODO: enum
)