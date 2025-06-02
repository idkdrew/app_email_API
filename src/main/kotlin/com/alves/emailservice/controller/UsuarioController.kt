package com.alves.emailservice.controller

import com.alves.emailservice.controller.dto.CadastroUsuarioRequest
import com.alves.emailservice.controller.dto.MensagemResponseDTO
import com.alves.emailservice.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
}