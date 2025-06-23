package com.alves.emailservice.controller.dto

import com.alves.emailservice.domain.model.Email

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

data class CriarRascunhoResponse(
    val mensagem: String,
    val rascunho: RascunhoResponse
)

data class RascunhoResponse(
    val rascunhoId: Long,
    val assunto: String?,
    val emailDestinatario: String?,
    val corpo: String?
)

data class RascunhosResponse(
    val mensagem: String,
    val rascunhos: List<RascunhoResponse>
)

data class EmailResponseDTO(
    val mensagem: String,
    val email: Email
)

data class ListarEmailsResponseDTO(
    val mensagem: String,
    val emails: List<Email>
)

data class ReadEmailResponseDTO(
    val emailId: Long,
    val assunto: String,
    val emailRemetente: String,
    val emailDestinatario: String,
    val corpo: String,
    val status: String,
    val dataEnvio: String
)

data class MarcarComoLidoResponseDTO(
    val mensagem: String,
    val email: ReadEmailResponseDTO
)
