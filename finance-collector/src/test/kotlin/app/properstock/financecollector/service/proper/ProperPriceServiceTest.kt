package app.properstock.financecollector.service.proper

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class ProperPriceServiceTest {

    @Autowired
    lateinit var properPriceService: ProperPriceService


    @Test
    fun calculate() {
        val x = properPriceService.calculate("005930")
        println()
    }

    @Test
    fun updateAll() {
        properPriceService.updateAll()
    }

    @Test
    fun update() {
        properPriceService.update("005930")
    }
}