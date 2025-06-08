package com.alves.emailservice.controller.dto

data class LoginResponse(
    val token: String
)

data class MensagemResponseDTO(val mensagem: String)

data class UsuarioResponse(
    val nome: String,
    val email: String
)

data class BuscarUsuarioResponse(
    val mensagem: String,
    val usuario: UsuarioResponse
)

data class ErroResponseDTO(
    val mensagem: String,
    val erro: String
)

data class ErroUsuarioResponseDTO(
    val mensagem: String
)