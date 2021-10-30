package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.proper.ProperPriceService
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
    val financeAnalysisRepository: FinanceAnalysisRepository,
    val properPriceService: ProperPriceService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    @Scheduled(cron = "0 0 1 * * *", zone = TZ_KR)
    fun updateTickers() {
        logger.info("Starting to update tickers...")
        naverFinanceCrawler
            .crawlAllTickers()
            .forEach {
                // todo: 업데이트 제대로
                if (tickerRepository.existsByCode(it.code)) tickerRepository.deleteByCode(it.code)
                tickerRepository.save(it)
                logger.info("${it.code}:${it.name} updated.")
            }
    }

    @Scheduled(cron = "0 0 2 * * *", zone = TZ_KR)
    fun updateFinanceData() {
        logger.info("Starting to update finance analysis...")
        tickerRepository
            .findAll()
            .forEach {
                try {
                    // 재무제표 업데이트
                    val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                    // todo: 업데이트 제대로
                    if (financeAnalysisRepository.existsByCode(it.code)) financeAnalysisRepository.deleteByCode(it.code)
                    financeAnalysisRepository.save(financeAnalysis)
                    logger.info("financeAnalysis@${it.code} updated successfully.")

                    // 적정주가 업데이트
                    properPriceService.update(it.code)
                } catch (e: Throwable) {
                    logger.warn("Failed to update ${it.code} caused by ${e.javaClass.simpleName}:${e.message}")
                }
            }
    }
}