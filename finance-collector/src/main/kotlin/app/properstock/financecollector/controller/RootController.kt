package app.properstock.financecollector.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {

    val state = State(
        status = "OK"
    )

    @GetMapping
    fun state() = state
}

data class State(
    val status: String
)