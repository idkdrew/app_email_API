package com.alves.emailservice.service

import com.alves.emailservice.domain.repository.TokenBlacklistRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val blacklistRepository: TokenBlacklistRepository
) {
    private val secret = "uma-chave-bem-grande-e-segura-mesmo-1234567890"
    private val expirationMillis = 1000 * 60 * 60 // 1 hora
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun gerarToken(email: String, id: Long): String {
        val agora = Date()
        val expiracao = Date(agora.time + expirationMillis)

        return Jwts.builder()
            .setSubject(email)
            .claim("id", id)
            .setIssuedAt(agora)
            .setExpiration(expiracao)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validarToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            val expirado = claims.expiration.before(Date())

            val tokenNaBlacklist = blacklistRepository.findByToken(token).isPresent

            !expirado && !tokenNaBlacklist
        } catch (e: Exception) {
            false
        }
    }

    fun getEmail(token: String): String {
        return getClaims(token).subject
    }

    fun getUsuarioId(token: String): Long? {
        return try {
            getClaims(token)["id"].toString().toLong()
        } catch (e: Exception) {
            return null
        }
    }

    fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
