package com.alves.emailservice.infra

import com.alves.emailservice.controller.dto.ErroResponseDTO
import com.alves.emailservice.controller.dto.ErroUsuarioResponseDTO
import com.alves.emailservice.exception.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ErroRequisicaoException::class)
    fun handleErroRequisicao(ex: ErroRequisicaoException, request: HttpServletRequest): ResponseEntity<ErroResponseDTO> {
        val erro = ErroResponseDTO(
            erro = ex.message ?: "",
            mensagem = "Erro na requisicao",
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro)
    }

    @ExceptionHandler(InternalServerErrorException::class)
    fun handleException(ex: InternalServerErrorException, request: HttpServletRequest): ResponseEntity<ErroResponseDTO> {
        val erro = ErroResponseDTO(
            erro = ex.message ?: "",
            mensagem = "Erro interno no servidor",
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro)
    }

    @ExceptionHandler(ErroNaoAutorizadoException::class)
    fun handleErroNaoAutorizadoException(ex: ErroNaoAutorizadoException, request: HttpServletRequest): ResponseEntity<ErroUsuarioResponseDTO> {
        val erro = ErroUsuarioResponseDTO( mensagem = "Usuario nao autorizado" )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro)
    }

    @ExceptionHandler(ErroUsuarioNaoEncontradoException::class)
    fun handleErroUsuarioNaoEncontrado(ex: ErroUsuarioNaoEncontradoException, request: HttpServletRequest): ResponseEntity<ErroUsuarioResponseDTO> {
        val erro = ErroUsuarioResponseDTO( mensagem = "Usuario nao encontrado" )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro)
    }

    @ExceptionHandler(CredenciaisInvalidasException::class)
    fun handleCredenciaisInvalidas(ex: CredenciaisInvalidasException): ResponseEntity<ErroUsuarioResponseDTO> {
        val erro = ErroUsuarioResponseDTO( mensagem = "Credenciais Incorretas" )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro)
    }
}
