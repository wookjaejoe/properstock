package app.properstock.financecollector.service

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.CorpStat
import app.properstock.financecollector.model.Industry
import app.properstock.financecollector.model.Theme
import app.properstock.financecollector.repository.*
import app.properstock.financecollector.service.proper.ProperPriceService
import org.openqa.selenium.WebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FinanceUpdater(
    val naverFinanceCrawler: NaverFinanceCrawler,
    val tickerRepository: TickerRepository,
    val corpStatRepository: CorpStatRepository,
    val finAnalRepository: FinanceAnalRepository,
    val properPriceService: ProperPriceService,
    val industryRepository: IndustryRepository,
    val themeRepository: ThemeRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(FinanceUpdater::class.java)

    fun updateTickers(webDriver: WebDriver) {
        logger.info("Starting to update tickers...")
        naverFinanceCrawler
            .crawlAllTickers(webDriver)
            .forEach {
                val oldData = tickerRepository.findByCode(it.code)
                if (oldData != null) {
                    oldData.apply {
                        this.name = it.name
                        this.price = it.price
                        this.marketCap = it.marketCap
                        this.shares = it.shares
                        this.per = it.per
                        this.roe = it.roe
                        this.externalLinks = it.externalLinks
                    }
                    tickerRepository.save(oldData)
                } else {
                    tickerRepository.save(it)
                }
                logger.info("Updated: $it")
            }
    }

    fun updateIndustries(webDriver: WebDriver) {
        logger.info("Starting to update industries...")
        naverFinanceCrawler
            .crawlIndustries(webDriver)
            .forEach { industry ->
                val codes = industry.tickerRefs.map { tickerRef -> tickerRef.split("code=")[1] }

                // industry 업데이트
                val oldData = industryRepository.findByName(industry.name)
                if (oldData != null) {
                    oldData.tickerCodes = codes
                    oldData.marginRate = industry.marginRate
                    oldData.timestamp = Instant.now()
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
    }

    @Deprecated(message = "너무 오래 걸림")
    fun updateThemes(webDriver: WebDriver) {
        logger.info("Starting to update themes...")
        naverFinanceCrawler.crawlThemes(webDriver)
            .forEach { theme ->
                // 테마 업데이트
                val codes = theme.tickerRefs.map { ref -> ref.split("code=")[1] }
                val oldData = themeRepository.findByName(theme.name)
                if (oldData != null) {
                    oldData.apply {
                        tickerCodes = codes
                        marginRate = theme.marginRate
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
                tickerRepository.save(ticker)
                logger.info("Updated: $ticker")
            }
    }

    private fun updateCorpStat(webDriver: WebDriver, code: String): CorpStat {
        // 기업현황 크롤링
        val corpStat = naverFinanceCrawler.crawlCorpStat(webDriver, code)
        // 이전 데이터
        val oldCorpStat = corpStatRepository.findByCode(code)
        return if (oldCorpStat != null) {
            oldCorpStat.apply {
                this.code = corpStat.code
                this.investOpinion = corpStat.investOpinion
                this.financeSummaries = corpStat.financeSummaries
            }
            corpStatRepository.save(oldCorpStat)
        } else {
            corpStatRepository.save(corpStat)
        }
    }

    private fun updateFinanceAnal(webDriver: WebDriver, code: String) {
        // 재무분석 크롤링
        val finAnal = naverFinanceCrawler.crawlFinanceAnal(webDriver, code)
        val oldFinAnal = finAnalRepository.findByCode(code)
        if (oldFinAnal != null) {
            oldFinAnal.financeStat = finAnal.financeStat
            finAnalRepository.save(oldFinAnal)
        } else {
            finAnalRepository.save(finAnal)
        }
    }

    fun updateFinanceData(webDriver: WebDriver) {
        val excludes = naverFinanceCrawler.run { crawlEtfCodes(webDriver) + crawlEtnCodes(webDriver) }
        val tickers = tickerRepository.findAll().filter { !excludes.contains(it.code) }
        tickers.forEachIndexed { index, ticker ->
            logger.info("[${index + 1}/${tickers.size}] Starting to update finance data for ${ticker.code}")
            try {
                // 기업현황 업데이트
                val corpStat = updateCorpStat(webDriver, ticker.code)
                ticker.targetPrice = corpStat.investOpinion?.targetPrice
                tickerRepository.save(ticker)
            } catch (e: Throwable) {
                logger.warn("Failed to update corpStat@${ticker.code} caused by ${e.javaClass.simpleName}: ${e.message}")
            }

            try {
                // 재무분석 업데이트
                updateFinanceAnal(webDriver, ticker.code)
            } catch (e: Throwable) {
                logger.warn("Failed to update financeAnal@${ticker.code} caused by ${e.javaClass.simpleName}: ${e.message}")
            }

            try {
                // 적정주가 업데이트
                properPriceService.update(ticker.code)
            } catch (e: Throwable) {
                logger.warn("Failed to update ${ticker.code} caused by ${e.javaClass.simpleName}:${e.message}")
            }
        }
    }

    fun updateTickerFromCorpStat() {
        tickerRepository.findAll()
            .filter { it.roe == null || it.roe!!.isNaN() }
            .mapNotNull {
                val roe = corpStatRepository.findByCode(it.code)
                    ?.financeSummaries
                    ?.get(CorpStat.FinanceSummary.Period.QUARTER)
                    ?.roe
                    ?.nearest()
                if (roe != null && !roe.isNaN()) {
                    it.apply { it.roe = roe }
                } else {
                    null
                }
            }.let {
                tickerRepository.saveAll(it)
            }
    }
}