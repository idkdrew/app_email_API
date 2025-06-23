package com.alves.emailservice.controller

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.exception.ErroNaoAutorizadoException
import com.alves.emailservice.service.RascunhoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rascunhos")
class RascunhoController(
    private val service: RascunhoService
) {
    @PostMapping
    fun criar(
        @RequestHeader("Authorization", defaultValue = "") authHeader: String,
        @RequestBody request: CriarRascunhoRequest
    ): ResponseEntity<CriarRascunhoResponse> {
        if (authHeader.isBlank()) {
            throw ErroNaoAutorizadoException()
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.criarRascunho(token, request)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PutMapping("/{id}")
    fun salvar(
        @PathVariable id: Long,
        @RequestHeader("Authorization", defaultValue = "") authHeader: String,
        @RequestBody request: SalvarRascunhoRequest
    ): ResponseEntity<CriarRascunhoResponse> {
        if (authHeader.isBlank()) {
            throw ErroNaoAutorizadoException()
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.salvarRascunho(token, request, id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long,
        @RequestHeader("Authorization", defaultValue = "") authHeader: String
    ): ResponseEntity<CriarRascunhoResponse> {
        if (authHeader.isBlank()) {
            throw ErroNaoAutorizadoException()
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.buscarRascunhoPorId(token, id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listarRascunhos(@RequestHeader("Authorization") authHeader: String): ResponseEntity<RascunhosResponse> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = service.listarRascunhos(token)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletarRascunho(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<MensagemResponseDTO> {
        val tokenLimpo = token.removePrefix("Bearer ").trim()
        val response = service.deletarRascunho(id, tokenLimpo)
        return ResponseEntity.ok(response)
    }
}