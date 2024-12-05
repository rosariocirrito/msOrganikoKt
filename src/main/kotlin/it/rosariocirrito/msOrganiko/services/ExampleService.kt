package it.rosariocirrito.msOrganiko.services

import org.springframework.stereotype.Service

@Service
class ExampleService {
    fun getNomeUtente(name: String) = "ciao $name"
}