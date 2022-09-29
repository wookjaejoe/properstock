package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import app.properstock.financecollector.crawl.WebDriverConnector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class ScheduledTasks(
    val webDriverConnector: WebDriverConnector,
    val financeUpdater: FinanceUpdater,
) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

    @Scheduled(cron = "0 0 22 * * *", zone = TZ_KR)
    fun updateTickerSummary() {
        logger.info("Starting to update ticker summary")
        webDriverConnector.connect {
            financeUpdater.updateTickers(this)
            financeUpdater.updateIndustries(this)
//            financeUpdater.updateThemes(this)
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = TZ_KR)
    fun updateFinanceData() {
        logger.info("Starting to update finance analysis...")
        webDriverConnector.connect {
            financeUpdater.updateFinanceData(this)
            financeUpdater.updateTickerFromCorpStat()
        }
    }
}