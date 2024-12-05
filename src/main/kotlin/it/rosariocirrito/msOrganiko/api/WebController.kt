package it.rosariocirrito.msOrganiko.api

import it.rosariocirrito.msOrganiko.services.ExampleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WebController {

    @Autowired
    lateinit var service: ExampleService

    @RequestMapping(value = ["/"], method =
    arrayOf(RequestMethod.GET))
    @ResponseBody
    fun hello() = "server on"

    @RequestMapping(value = ["/user/{name}"], method =
    arrayOf(RequestMethod.GET))
    @ResponseBody
    fun helloUser(@PathVariable name: String) = service.getNomeUtente(name)
}