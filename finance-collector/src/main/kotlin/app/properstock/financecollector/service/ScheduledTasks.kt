package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class ScheduledTasks(
    val financeUpdater: FinanceUpdater
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    @Scheduled(cron = "0 0 22 * * *", zone = TZ_KR)
    fun updateTickerSummary() {
        logger.info("Starting to update ticker summary")
        financeUpdater.updateTickers()
        financeUpdater.updateIndustries()
        financeUpdater.updateThemes()
    }

    @Scheduled(cron = "0 0 0 * * *", zone = TZ_KR)
    fun updateFinanceData() {
        FinanceUpdater.logger.info("Starting to update finance analysis...")
        financeUpdater.updateFinanceData()
        financeUpdater.updateTickerFromCorpStat()
    }
}