package com.alves.emailservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.runApplication

@SpringBootApplication
class EmailserviceApplication

fun main(args: Array<String>) {
    val dotenv = dotenv()
    dotenv["SERVER_PORT"]?.let {
        System.setProperty("server.port", it)
    }
    runApplication<EmailserviceApplication>(*args)
}
