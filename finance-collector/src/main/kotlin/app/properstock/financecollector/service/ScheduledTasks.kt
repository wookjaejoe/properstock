package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.Industry
import app.properstock.financecollector.model.Theme
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.IndustryRepository
import app.properstock.financecollector.repository.ThemeRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.proper.ProperPriceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@EnableScheduling
class ScheduledTasks(
    val naverFinanceCrawler: NaverFinanceCrawler,
    val tickerRepository: TickerRepository,
    val financeAnalysisRepository: FinanceAnalysisRepository,
    val properPriceService: ProperPriceService,
    val industryRepository: IndustryRepository,
    val themeRepository: ThemeRepository
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    @Scheduled(cron = "0 0 22 * * *", zone = TZ_KR)
    fun updateTickers() {
        logger.info("Starting to update tickers...")
        naverFinanceCrawler
            .crawlAllTickers()
            .forEach {
                val oldData = tickerRepository.findByCode(it.code)
                if (oldData != null) {
                    oldData.apply {
                        this.name = it.name
                        this.price = it.price
                        this.marketCap = it.marketCap
                        this.shares = it.shares
                        this.link = it.link
                    }
                    tickerRepository.save(oldData)
                } else {
                    tickerRepository.save(it)
                }
                logger.info("Updated: $it")
            }

        naverFinanceCrawler
            .crawlIndustries()
            .forEach { industry ->
                val codes = industry.tickerRefs.map { tickerRef -> tickerRef.split("code=")[1] }

                // industry 업데이트
                val oldData = industryRepository.findByName(industry.name)
                if (oldData != null) {
                    oldData.tickerCodes = codes
                    oldData.marginRate = industry.marginRate
                    oldData.updatedAt = Instant.now()
                    industryRepository.save(oldData)
                    logger.info("Updated: $oldData")
                } else {
                    val newData = Industry(
                        name = industry.name,
                        tickerCodes = codes,
                        marginRate = industry.marginRate
                    )
                    industryRepository.save(newData)
                    logger.info("Updated: $newData")
                }

                // 티커 업데이트
                codes.forEach { code ->
                    val ticker = tickerRepository.findByCode(code)
                    if (ticker != null) {
                        ticker.industry = industry.name
                        tickerRepository.save(ticker)
                        logger.info("Updated: $ticker")
                    }
                }
            }

        naverFinanceCrawler.crawlThemes()
            .forEach { theme ->
                // 테마 업데이트
                val codes = theme.tickerRefs.map { ref -> ref.split("code=")[1] }
                val oldData = themeRepository.findByName(theme.name)
                if (oldData != null) {
                    oldData.apply {
                        tickerCodes = codes
                        marginRate = theme.marginRate
                        updatedAt = Instant.now()
                        themeRepository.save(this)
                        logger.info("Updated: $this")
                    }
                } else {
                    val newData = Theme(
                        name = theme.name,
                        tickerCodes = codes,
                        marginRate = theme.marginRate
                    )
                    themeRepository.save(newData)
                    logger.info("Updated: $newData")
                }
            }

        // 티커의 테마 업데이트
        val themes = themeRepository.findAll()
        tickerRepository.findAll()
            .forEach { ticker ->
                val themeNames = themes.filter { theme -> theme.tickerCodes.contains(ticker.code) }
                    .map { theme -> theme.name }
                ticker.themes = themeNames
                ticker.updated = Instant.now()
                tickerRepository.save(ticker)
                logger.info("Updated: $ticker")
            }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = TZ_KR)
    fun updateFinanceData() {
        logger.info("Starting to update finance analysis...")
        tickerRepository
            .findAll()
            .forEach {
                try {
                    // 재무제표 업데이트
                    val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                    val oldData = financeAnalysisRepository.findByCode(it.code)
                    if (oldData != null) {
                        oldData.apply {
                            code = financeAnalysis.code
                            financeSummary = financeAnalysis.financeSummary
                            updated = Instant.now()
                        }
                        financeAnalysisRepository.save(oldData)
                    } else {
                        financeAnalysisRepository.save(financeAnalysis)
                    }

                    logger.info("financeAnalysis@${it.code} updated successfully.")

                    // 적정주가 업데이트
                    properPriceService.update(it.code)
                } catch (e: Throwable) {
                    logger.warn("Failed to update ${it.code} caused by ${e.javaClass.simpleName}:${e.message}")
                }
            }
    }
}