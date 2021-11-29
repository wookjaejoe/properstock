package app.properstock.financecollector.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandlers {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ExceptionHandlers::class.java)
    }

    fun handle(e: Throwable): ErrorMessage {
        logger.error(e.message, e)
        return ErrorMessage(e.message)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun keyValueNotMatchException(e: ResourceNotFoundException): ErrorMessage {
        return handle(e)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun others(e: Throwable): ErrorMessage {
        return handle(e)
    }
}

data class ErrorMessage(
    val message: String?
)