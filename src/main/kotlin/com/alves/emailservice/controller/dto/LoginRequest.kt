package com.alves.emailservice.controller.dto

data class LoginRequest(
    val email: String,
    val senha: String
)