package app.properstock.financecollector.service

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class ScheduledTasks(
    val naverFinanceCrawler: NaverFinanceCrawler,
    val tickerRepository: TickerRepository,
    val financeAnalysisRepository: FinanceAnalysisRepository
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    @Scheduled(cron = "0 0 4,16 * * *")
    fun updateTickers() {
        logger.info("Starting to update tickers...")
        naverFinanceCrawler.crawlAllTickers()
            .flatMap {
                tickerRepository.save(it)
            }
            .subscribe(
                {
                    logger.info("Successfully updated: ${it.code}")
                },
                {
                    logger.error("An error occurs while updating tickers...", it)
                }
            )
    }

    @Scheduled(cron = "0 0 4,16 * * *")
    fun updateFinanceAnalysis() {
        tickerRepository
            .findAll()
            .collectList()
            .block()
            ?.map {
                try {
                    val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                    logger.info("${it.code} updated successfully.")
                    financeAnalysis
                } catch (e: Throwable) {
                    logger.warn("Failed to update ${it.code} caused by ${e.javaClass.simpleName}:${e.message}")
                    null
                }
            }
            ?.map { it?.apply { financeAnalysisRepository.save(it).block() } }
    }
}