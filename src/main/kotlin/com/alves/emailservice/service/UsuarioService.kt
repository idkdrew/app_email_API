package com.alves.emailservice.service

import com.alves.emailservice.controller.dto.*
import com.alves.emailservice.domain.model.TokenAtivo
import com.alves.emailservice.domain.model.TokenBlacklist
import com.alves.emailservice.domain.model.Usuario
import com.alves.emailservice.domain.repository.TokenAtivoRepository
import com.alves.emailservice.domain.repository.TokenBlacklistRepository
import com.alves.emailservice.domain.repository.UsuarioRepository
import com.alves.emailservice.exception.CredenciaisInvalidasException
import com.alves.emailservice.exception.ErroNaoAutorizadoException
import com.alves.emailservice.exception.ErroUsuarioNaoEncontradoException
import com.alves.emailservice.exception.ErroRequisicaoException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZoneId

@Service
class UsuarioService(
    private val repository: UsuarioRepository,
    private val encoder: BCryptPasswordEncoder,
    private val jwtService: JwtService,
    private val blacklistRepository: TokenBlacklistRepository,
    private val authenticationManager: AuthenticationManager,
    private val tokenAtivoRepository: TokenAtivoRepository
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

    fun realizarLogin(request: LoginRequest): LoginResponse {
        try {
            val auth = UsernamePasswordAuthenticationToken(request.email, request.senha)
            authenticationManager.authenticate(auth)
        } catch (ex: BadCredentialsException) {
            throw CredenciaisInvalidasException()
        } catch (ex: Exception) {
            throw ErroRequisicaoException()
        }

        val usuario = repository.findByEmailAndAtivoTrue(request.email)
            ?: throw CredenciaisInvalidasException()

        val id = usuario.id ?: throw ErroRequisicaoException()

        val token = jwtService.gerarToken(id)

        val expiration = jwtService.getClaims(token).expiration
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

        tokenAtivoRepository.save(TokenAtivo(token, id, expiration))

        return LoginResponse(token)
    }

    fun buscarUsuarioPorToken(token: String): BuscarUsuarioResponse {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow{ ErroRequisicaoException() }

        if (!usuario.ativo) throw ErroUsuarioNaoEncontradoException()

        return BuscarUsuarioResponse(
            mensagem = "Sucesso ao buscar usuario",
            usuario = UsuarioResponse(
                nome = usuario.nome,
                email = usuario.email
            )
        )
    }

    fun logoutUsuario(token: String): MensagemResponseDTO {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow { ErroUsuarioNaoEncontradoException() }

        if (!usuario.ativo) throw ErroUsuarioNaoEncontradoException()

        val claims = jwtService.getClaims(token)

        val expiration = try {
            claims.expiration
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            throw ErroRequisicaoException()
        }

        tokenAtivoRepository.deleteById(token)
        val blacklist = TokenBlacklist(token = token, expiration = expiration)
        blacklistRepository.save(blacklist)

        return MensagemResponseDTO("Logout realizado com sucesso")
    }

    fun atualizarUsuario(token: String, request: AtualizarUsuarioRequest): BuscarUsuarioResponse {
        val id = jwtService.getUsuarioId(token)
            ?: throw ErroNaoAutorizadoException()

        val usuario = repository.findById(id)
            .orElseThrow { ErroUsuarioNaoEncontradoException() }

        if (!usuario.ativo) throw ErroUsuarioNaoEncontradoException()

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
            .orElseThrow { ErroUsuarioNaoEncontradoException() }

        if (!usuario.ativo) throw ErroUsuarioNaoEncontradoException()

        usuario.ativo = false
        repository.save(usuario)

        return MensagemResponseDTO("Usuário deletado com sucesso")
    }

    fun listarUsuariosLogados(): List<UsuarioResponse> {
        val tokensValidos = tokenAtivoRepository.findAllByExpirationAfter()
        val ids = tokensValidos.map { it.userId }.distinct()
        val usuarios = repository.findAllById(ids)

        return usuarios
            .filter { it.ativo }
            .map { UsuarioResponse(nome = it.nome, email = it.email) }
    }
}