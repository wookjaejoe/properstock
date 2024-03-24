package app.properstock.financecollector

import app.properstock.financecollector.service.ScheduledTasks
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class ScheduledTasksTest @Autowired constructor(
    val scheduledTasks: ScheduledTasks
) {
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

    @Test
    fun test() {
        scheduledTasks.updateFnData()
    }
}