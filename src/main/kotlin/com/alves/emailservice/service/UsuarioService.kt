package com.alves.emailservice.service

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.domain.model.TokenBlacklist
import com.alves.emailservice.domain.model.Usuario
import com.alves.emailservice.domain.repository.TokenBlacklistRepository
import com.alves.emailservice.domain.repository.UsuarioRepository
import com.alves.emailservice.exception.ErroNaoAutorizadoException
import com.alves.emailservice.exception.ErroUsuarioNaoEncontradoException
import com.alves.emailservice.exception.ErroRequisicaoException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val repository: UsuarioRepository,
    private val encoder: BCryptPasswordEncoder,
    private val jwtService: JwtService,
    private val blacklistRepository: TokenBlacklistRepository
) {
    private val EMAIL_REGEX = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    private fun validarCadastro(request: CadastroUsuarioRequest) {
        if (request.nome.isBlank() || request.nome.length > 255) {
            throw ErroRequisicaoException()
        }

        if (!EMAIL_REGEX.matches(request.email)) {
            throw ErroRequisicaoException()
        }

        if (request.senha.length < 8 || request.senha.length > 20) {
            throw ErroRequisicaoException()
        }
    }

    private fun validarAtualizacao(request: AtualizarUsuarioRequest) {
        if (request.nome.isBlank() || request.nome.length > 255) {
            throw ErroRequisicaoException()
        }

        if (request.senha.length < 8 || request.senha.length > 20) {
            throw ErroRequisicaoException()
        }
    }

    fun cadastrar(request: CadastroUsuarioRequest): Usuario{
        validarCadastro(request)

        if(repository.findByEmail(request.email) != null) {
            throw ErroRequisicaoException()
        }

        val usuario = Usuario(
            nome = request.nome,
            email = request.email,
            senha = encoder.encode(request.senha)
        )

        return repository.save(usuario)
    }

    fun buscarPorEmail(login: LoginRequest): Usuario {
        val usuario = repository.findByEmail(login.email)
            ?: throw RuntimeException("Usuário não encontrado")

        return usuario
    }

    fun buscarUsuarioPorToken(token: String): BuscarUsuarioResponse {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow{ ErroRequisicaoException() }

        return BuscarUsuarioResponse(
            mensagem = "Sucesso ao buscar usuario",
            usuario = UsuarioResponse(
                nome = usuario.nome,
                email = usuario.email
            )
        )
    }

    fun logoutUsuario(token: String): MensagemResponseDTO {
        val claims = jwtService.getClaims(token)
        val expiration = claims.expiration.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()

        val blacklist = TokenBlacklist(token = token, expiration = expiration)
        blacklistRepository.save(blacklist)

        return MensagemResponseDTO("Logout realizado com sucesso")
    }

    fun atualizarUsuario(token: String, request: AtualizarUsuarioRequest): BuscarUsuarioResponse {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow { ErroUsuarioNaoEncontradoException() }

        validarAtualizacao(request)

        usuario.nome = request.nome
        usuario.senha = encoder.encode(request.senha)

        repository.save(usuario)

        return BuscarUsuarioResponse(
            mensagem = "Sucesso ao atualizar usuário",
            usuario = UsuarioResponse(
                nome = usuario.nome,
                email = usuario.email
            )
        )
    }


    fun deletarUsuario(token: String): MensagemResponseDTO {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        repository.delete(usuario)

        return MensagemResponseDTO("Usuário deletado com sucesso")
    }
}