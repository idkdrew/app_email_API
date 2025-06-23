package com.alves.emailservice.service

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.domain.model.Rascunho
import com.alves.emailservice.domain.repository.RascunhoRepository
import com.alves.emailservice.exception.*
import org.springframework.stereotype.Service

@Service
class RascunhoService(
    private val rascunhoRepository: RascunhoRepository,
    private val jwtService: JwtService
) {
    fun criarRascunho(token: String, request: CriarRascunhoRequest): CriarRascunhoResponse {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            validarRascunhoRequest(request.assunto, request.emailDestinatario, request.corpo)

            val rascunho = Rascunho(
                usuarioId = usuarioId,
                assunto = request.assunto?.trim(),
                emailDestinatario = request.emailDestinatario?.trim(),
                corpo = request.corpo?.trim()
            )

            val salvo = rascunhoRepository.save(rascunho)

            return CriarRascunhoResponse(
                mensagem = "Rascunho criado",
                rascunho = RascunhoResponse(
                    rascunhoId = salvo.rascunhoId!!,
                    assunto = salvo.assunto,
                    emailDestinatario = salvo.emailDestinatario,
                    corpo = salvo.corpo
                )
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRequisicaoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }

    fun salvarRascunho(token: String, request: SalvarRascunhoRequest, id: Long): CriarRascunhoResponse {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            if(id < 0){
                throw ErroRequisicaoException()
            }

            validarRascunhoRequest(request.assunto, request.emailDestinatario, request.corpo)

            val rascunho = rascunhoRepository.findById(id)
                .orElseThrow { ErroRascunhoNaoEncontradoException() }

            if (rascunho.usuarioId != usuarioId) {
                throw ErroNaoAutorizadoException()
            }

            val atualizado = rascunho.copy(
                assunto = request.assunto ?: rascunho.assunto,
                emailDestinatario = request.emailDestinatario ?: rascunho.emailDestinatario,
                corpo = request.corpo ?: rascunho.corpo
            )

            val salvo = rascunhoRepository.save(atualizado)

            return CriarRascunhoResponse(
                mensagem = "Rascunho salvo com sucesso",
                rascunho = RascunhoResponse(
                    rascunhoId = salvo.rascunhoId!!,
                    assunto = salvo.assunto,
                    emailDestinatario = salvo.emailDestinatario,
                    corpo = salvo.corpo
                )
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRequisicaoException) {
            throw e
        } catch (e: ErroUsuarioNaoEncontradoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }


    fun buscarRascunhoPorId(token: String, rascunhoId: Long): CriarRascunhoResponse {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow { throw ErroRascunhoNaoEncontradoException() }

            if (rascunho.usuarioId != usuarioId) {
                throw ErroNaoAutorizadoException()
            }

            return CriarRascunhoResponse(
                mensagem = "Rascunho localizado",
                rascunho = RascunhoResponse(
                    rascunhoId = rascunho.rascunhoId!!,
                    assunto = rascunho.assunto,
                    emailDestinatario = rascunho.emailDestinatario,
                    corpo = rascunho.corpo
                )
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRascunhoNaoEncontradoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }


    fun listarRascunhos(token: String): RascunhosResponse {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val rascunhos = rascunhoRepository.findAllByUsuarioId(usuarioId)

            if (rascunhos.isEmpty()) {
                throw ErroRascunhoNaoEncontradoException()
            }

            return RascunhosResponse(
                mensagem = "Rascunhos encontrados",
                rascunhos = rascunhos.map {
                    RascunhoResponse(
                        rascunhoId = it.rascunhoId!!,
                        assunto = it.assunto,
                        emailDestinatario = it.emailDestinatario,
                        corpo = it.corpo
                    )
                }
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRascunhoNaoEncontradoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }


    fun deletarRascunho(id: Long, token: String): MensagemResponseDTO {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val rascunho = rascunhoRepository.findById(id)
                .orElseThrow { ErroRascunhoNaoEncontradoException() }

            if (rascunho.usuarioId != usuarioId) {
                throw ErroNaoAutorizadoException()
            }

            rascunhoRepository.delete(rascunho)

            return MensagemResponseDTO("Rascunho deletado com sucesso")
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRascunhoNaoEncontradoException) {
            throw e
        } catch (e: ErroRequisicaoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }

    fun validarRascunhoRequest(assunto: String?, emailDestinatario: String?, corpo: String?) {
        if ((assunto.isNullOrBlank() && emailDestinatario.isNullOrBlank() && corpo.isNullOrBlank())) {
            throw ErroRequisicaoException()
        }

        if (!assunto.isNullOrBlank() && (assunto.length !in 1..255)) {
            throw ErroRequisicaoException()
        }

        val emailRegex = Regex("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        if (!emailDestinatario.isNullOrBlank() && !emailRegex.matches(emailDestinatario)) {
            throw ErroRequisicaoException()
        }
    }
}
