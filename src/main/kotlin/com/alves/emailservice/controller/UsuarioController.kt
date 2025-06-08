package com.alves.emailservice.controller

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(
    private val service: UsuarioService
) {
    @PostMapping
    fun cadastrarUsuario(@RequestBody @Valid request: CadastroUsuarioRequest): ResponseEntity<MensagemResponseDTO> {
        service.cadastrar(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MensagemResponseDTO("Sucesso ao cadastrar usuario"))
    }

    @GetMapping
    fun getUsuarioLogado(@RequestHeader("Authorization") authHeader: String): ResponseEntity<BuscarUsuarioResponse> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.buscarUsuarioPorToken(token)
        return ResponseEntity.ok(response)
    }

    @PutMapping
    fun atualizarUsuario(
        @RequestHeader("Authorization") token: String,
        @RequestBody @Valid request: AtualizarUsuarioRequest
    ): ResponseEntity<MensagemResponseDTO> {
        val response = service.atualizarUsuario(token.removePrefix("Bearer ").trim(), request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping
    fun deletarUsuario(@RequestHeader("Authorization") authHeader: String): ResponseEntity<MensagemResponseDTO> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.deletarUsuario(token)
        return ResponseEntity.ok(response)
    }
}