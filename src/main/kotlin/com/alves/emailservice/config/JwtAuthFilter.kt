package com.alves.emailservice.config

import com.alves.emailservice.domain.repository.UsuarioRepository
import com.alves.emailservice.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthFilter(
    private val jwtService: JwtService,
    private val usuarioRepository: UsuarioRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()

            if (jwtService.validarToken(token)) {
                val usuarioId = jwtService.getUsuarioId(token)
                val usuario = usuarioId?.let { usuarioRepository.findById(it).orElse(null) }

                if (usuario != null) {
                    val userDetails = org.springframework.security.core.userdetails.User(
                        usuario.email,
                        usuario.senha,
                        emptyList()
                    )
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}

