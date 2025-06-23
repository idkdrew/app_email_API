package com.alves.emailservice.service

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.domain.repository.EmailRepository
import com.alves.emailservice.domain.model.Email
import com.alves.emailservice.domain.repository.RascunhoRepository
import com.alves.emailservice.domain.repository.UsuarioRepository
import com.alves.emailservice.exception.*
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val emailRepository: EmailRepository,
    private val rascunhoRepository: RascunhoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val jwtService: JwtService
){
    fun enviarEmail(rascunhoId: Long, token: String): EmailResponseDTO {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow { throw ErroNaoAutorizadoException() }

            val rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow { ErroRascunhoNaoEncontradoException() }

            if (rascunho.usuarioId != usuarioId) {
                throw ErroNaoAutorizadoException()
            }

            validarEmailCamposObrigatorios(rascunho.assunto, rascunho.emailDestinatario, rascunho.corpo)

            val email = Email(
                assunto = rascunho.assunto!!,
                emailRemetente = usuario.email,
                emailDestinatario = rascunho.emailDestinatario!!,
                corpo = rascunho.corpo!!,
                status = "enviado",
                dataEnvio = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            )

            val salvo = emailRepository.save(email)
            rascunhoRepository.delete(rascunho)

            return EmailResponseDTO(
                mensagem = "Email enviado com sucesso",
                email = salvo
            )
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

    fun enviarEmailDireto(request: EnviarEmailRequest, token: String): EmailResponseDTO {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow { throw ErroNaoAutorizadoException() }

            validarEmailCamposObrigatorios(request.assunto, request.emailDestinatario, request.corpo)

            val assunto = request.assunto!!
            val destinatario = request.emailDestinatario!!
            val corpo = request.corpo!!

            val email = Email(
                assunto = assunto,
                emailRemetente = usuario.email,
                emailDestinatario = destinatario,
                corpo = corpo,
                status = "enviado",
                dataEnvio = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            )

            val salvo = emailRepository.save(email)

            return EmailResponseDTO(
                mensagem = "Email enviado com sucesso",
                email = salvo
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroRequisicaoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }

    fun listarEmailsPorUsuario(token: String): ListarEmailsResponseDTO {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow { throw ErroNaoAutorizadoException() }

            val emails = emailRepository.findAllByEmailDestinatario(usuario.email)

            if (emails.isEmpty()) {
                throw ErroEmailNaoEncontradoException()
            }

            return ListarEmailsResponseDTO(
                mensagem = "Emails encontrados",
                emails = emails
            )
        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroEmailNaoEncontradoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }

    fun marcarEmailComoLido(emailId: Long, token: String): MarcarComoLidoResponseDTO {
        try {
            val usuarioId = jwtService.getUsuarioId(token)
                ?: throw ErroNaoAutorizadoException()

            val usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow { throw ErroNaoAutorizadoException() }

            val email = emailRepository.findById(emailId)
                .orElseThrow { throw ErroEmailNaoEncontradoException() }

            if (email.emailDestinatario != usuario.email && email.emailRemetente != usuario.email) {
                throw ErroNaoAutorizadoException()
            }

            email.status = "lido"
            val atualizado = emailRepository.save(email)

            val response = ReadEmailResponseDTO(
                emailId = atualizado.emailId!!,
                assunto = atualizado.assunto,
                emailRemetente = atualizado.emailRemetente,
                emailDestinatario = atualizado.emailDestinatario,
                corpo = atualizado.corpo,
                status = atualizado.status,
                dataEnvio = atualizado.dataEnvio.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            )

            return MarcarComoLidoResponseDTO("Email marcado como lido", response)

        } catch (e: ErroNaoAutorizadoException) {
            throw e
        } catch (e: ErroEmailNaoEncontradoException) {
            throw e
        } catch (e: ErroRequisicaoException) {
            throw e
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }
    }

    fun validarEmailCamposObrigatorios(assunto: String?, emailDestinatario: String?, corpo: String?) {
        if (assunto.isNullOrBlank()) {
            throw ErroRequisicaoException()
        }

        if (assunto.length !in 1..255) {
            throw ErroRequisicaoException()
        }

        val emailRegex = Regex("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        if (emailDestinatario.isNullOrBlank() || !emailRegex.matches(emailDestinatario)) {
            throw ErroRequisicaoException()
        }

        if (corpo.isNullOrBlank()) {
            throw ErroRequisicaoException()
        }
    }
}