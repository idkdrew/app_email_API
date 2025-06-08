package com.alves.emailservice.controller.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    val email: String,
    val senha: String
)

data class CadastroUsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String
)

data class AtualizarUsuarioRequest(
    @field:NotBlank val nome: String,
    @field:NotBlank val senha: String
)

