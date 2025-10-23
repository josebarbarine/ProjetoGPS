package com.example.projetogps
data class Weather(
    val id: Int = 0,
    val cidade: String = "",
    val temperatura: String = "",
    val descricao: String = "",
    val umidade: String = "",
    val velocidadeVento: String = "",
    val dataHora: String = ""
)