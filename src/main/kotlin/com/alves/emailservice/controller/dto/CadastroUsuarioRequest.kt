package com.alves.emailservice.controller.dto

data class CadastroUsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String
)