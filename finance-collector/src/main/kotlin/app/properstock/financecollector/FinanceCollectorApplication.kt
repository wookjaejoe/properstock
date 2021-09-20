package app.properstock.financecollector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinanceCollectorApplication

fun main(args: Array<String>) {
    runApplication<FinanceCollectorApplication>(*args)
}
