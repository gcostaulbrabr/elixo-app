package com.example.myapplication

import java.time.ZonedDateTime

data class Coleta(
    val id: String = "",
    val usuarioId: String = "",
    val prestadorId: String = "",
    val endereco: String = "",
    val dataHora: ZonedDateTime,
    val aparelhoTipo: String = "",
    val status: Int // TODO: enum
)