package com.alves.emailservice.service

import com.alves.emailservice.controller.dto.CadastroUsuarioRequest
import com.alves.emailservice.domain.model.Usuario
import com.alves.emailservice.domain.repository.UsuarioRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService (
    private val repository: UsuarioRepository,
    private val encoder: BCryptPasswordEncoder
) {
    fun cadastrar(request: CadastroUsuarioRequest): Usuario{
        if(repository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email já cadastrado")
        }

        val usuario = Usuario(
            nome = request.nome,
            email = request.email,
            senha = encoder.encode(request.senha)
        )

        return repository.save(usuario)
    }

}