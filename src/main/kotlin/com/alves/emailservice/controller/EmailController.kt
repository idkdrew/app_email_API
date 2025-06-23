package com.alves.emailservice.controller

import com.alves.emailservice.controller.dto.EmailResponseDTO
import com.alves.emailservice.controller.dto.EnviarEmailRequest
import com.alves.emailservice.controller.dto.ListarEmailsResponseDTO
import com.alves.emailservice.controller.dto.MarcarComoLidoResponseDTO
import com.alves.emailservice.service.EmailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/emails")
class EmailController(
    private val service: EmailService
) {
    @PostMapping
    fun enviarEmailDireto(
        @RequestBody request: EnviarEmailRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<EmailResponseDTO> {
        val tokenLimpo = token.removePrefix("Bearer ").trim()
        val response = service.enviarEmailDireto(request, tokenLimpo)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}")
    fun enviarEmailPorRascunho(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<EmailResponseDTO> {
        val tokenLimpo = token.removePrefix("Bearer ").trim()
        val response = service.enviarEmail(id, tokenLimpo)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listarEmails(@RequestHeader("Authorization") token: String): ResponseEntity<ListarEmailsResponseDTO> {
        val tokenLimpo = token.removePrefix("Bearer ").trim()
        val response = service.listarEmailsPorUsuario(tokenLimpo)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun marcarEmailComoLido(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<MarcarComoLidoResponseDTO> {
        val tokenLimpo = token.removePrefix("Bearer ").trim()
        val response = service.marcarEmailComoLido(id, tokenLimpo)
        return ResponseEntity.ok(response)
    }

}