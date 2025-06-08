package com.alves.emailservice.controller

import com.alves.emailservice.controller.dto.LoginRequest
import com.alves.emailservice.controller.dto.LoginResponse
import com.alves.emailservice.controller.dto.MensagemResponseDTO
import com.alves.emailservice.service.JwtService
import com.alves.emailservice.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class AuthController(
    private val authManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val service: UsuarioService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val auth = UsernamePasswordAuthenticationToken(request.email, request.senha)
        authManager.authenticate(auth) // dispara a verificação

        val usuario = service.buscarPorEmail(request)

        usuario.id?.let {
            val token = jwtService.gerarToken(usuario.email, it)
            return ResponseEntity.ok(LoginResponse(token))
        } ?: throw RuntimeException("Usuário sem ID válido")
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authHeader: String): ResponseEntity<MensagemResponseDTO> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.logoutUsuario(token)
        return ResponseEntity.ok(response)
    }
}

