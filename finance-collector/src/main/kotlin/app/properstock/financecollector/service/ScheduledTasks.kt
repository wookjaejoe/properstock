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
        naverFinanceCrawler
            .crawlAllTickers()
            .forEach {
                if (tickerRepository.existsByCode(it.code)) tickerRepository.deleteByCode(it.code)
                tickerRepository.save(it)
                logger.info("${it.code}:${it.name} updated.")
            }
    }

    @Scheduled(cron = "0 0 5,17 * * *")
    fun updateFinanceAnalysis() {
        logger.info("Starting to update finance analysis...")
        tickerRepository
            .findAll()
            .forEach {
                try {
                    val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                    if (financeAnalysisRepository.existsByCode(it.code)) financeAnalysisRepository.deleteByCode(it.code)
                    financeAnalysisRepository.save(financeAnalysis)
                    logger.info("${it.code} updated successfully.")
                } catch (e: Throwable) {
                    logger.warn("Failed to update ${it.code} caused by ${e.javaClass.simpleName}:${e.message}")
                }
            }
    }
}