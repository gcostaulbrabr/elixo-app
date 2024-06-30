package com.example.myapplication

class ColetaDto(
    val id: String = "",
    val usuarioId: String = "",
    val prestadorId: String = "",
    val endereco: String = "",
    val dataHora: String = "",
    val aparelhoTipo: String = "",
    val status: Int = 0 // TODO: enum
)