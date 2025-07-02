package com.alves.emailservice.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UsuarioWebController {
    @GetMapping("/usuarios-logados")
    fun usuariosLogadosPage(): String {
        return "usuarios-logados"
    }
}
