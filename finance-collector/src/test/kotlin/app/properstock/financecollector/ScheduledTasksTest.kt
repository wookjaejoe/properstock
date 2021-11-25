package app.properstock.financecollector

import app.properstock.financecollector.service.ScheduledTasks
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class ScheduledTasksTest {

    @Autowired
    lateinit var scheduledTasks: ScheduledTasks

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    @Test
    fun updateTickerSummary() {
        scheduledTasks.updateTickerSummary()
    }

    @Test
    fun updateFinanceData() {
        scheduledTasks.updateFinanceData()
    }

    @Test
    fun updateAll() {
        scheduledTasks.updateTickerSummary()
        scheduledTasks.updateFinanceData()
    }
}