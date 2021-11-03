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

    /**
     * 티커 목록 업데이트
     */
    @Test
    fun updateTickers() {
        scheduledTasks.updateTickers()
    }

    @Test
    fun updateFinanceAnalysis() {
        scheduledTasks.updateFinanceData()
    }

    @Test
    fun updateAll() {
        scheduledTasks.updateTickers()
        scheduledTasks.updateFinanceData()
    }
}