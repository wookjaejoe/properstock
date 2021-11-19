package app.properstock.financecollector.controller

import app.properstock.financecollector.service.ScheduledTasks
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController(
    val scheduledTasks: ScheduledTasks
) {
    @PostMapping("/tickers")
    fun updateTickers() {
        scheduledTasks.updateTickerSummary()
    }

    @PostMapping("/finance-analysis")
    fun updateFinanceData() {
        scheduledTasks.updateFinanceData()
    }
}