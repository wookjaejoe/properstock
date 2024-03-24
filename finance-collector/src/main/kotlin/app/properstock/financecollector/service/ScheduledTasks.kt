package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import app.properstock.financecollector.crawl.WebDriverConnector
import app.properstock.financecollector.crawl.fn.FnInvestmentIndicatorCrawler
import app.properstock.financecollector.crawl.fn.FnTickerFinder
import app.properstock.financecollector.repository.FnInvestmentIndicatorRepository
import app.properstock.financecollector.repository.FnTickerRepository
import net.bytebuddy.implementation.bytecode.Throw
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
    val fnTickerRepository: FnTickerRepository,
    val fnInvestmentIndicatorRepository: FnInvestmentIndicatorRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

    //    @Scheduled(cron = "0 0 22 * * *", zone = TZ_KR)
    fun updateTickerSummary() {
        logger.info("Starting to update ticker summary")
        webDriverConnector.connect {
            financeUpdater.updateTickers(this)
            financeUpdater.updateIndustries(this)
        }
    }

    //    @Scheduled(cron = "0 0 0 * * *", zone = TZ_KR)
    fun updateFinanceData() {
        logger.info("Starting to update finance analysis...")
        webDriverConnector.connect {
            financeUpdater.updateFinanceData(this)
            financeUpdater.updateTickerFromCorpStat()
        }
    }

    @Scheduled(cron = "0 0 2 * * *", zone = TZ_KR)
    fun updateFnData() {
        val tickers = fnTickerRepository.saveAll(FnTickerFinder.findAll())
        tickers.forEach {
            try {
                val indicators = FnInvestmentIndicatorCrawler.crawl(it.cd)
                if (indicators != null) fnInvestmentIndicatorRepository.saveAll(indicators)
            } catch (e: Throwable) {
                logger.error(e.message)
            }
        }
    }
}