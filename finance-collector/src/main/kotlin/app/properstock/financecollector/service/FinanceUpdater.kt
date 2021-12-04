package app.properstock.financecollector.service

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.CorpStat
import app.properstock.financecollector.model.Industry
import app.properstock.financecollector.model.Theme
import app.properstock.financecollector.repository.*
import app.properstock.financecollector.service.proper.ProperPriceService
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
    val themeRepository: ThemeRepository
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(FinanceUpdater::class.java)
    }

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

    fun updateIndustries() {
        logger.info("Starting to update industries...")
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
    }

    fun updateThemes() {
        logger.info("Starting to update themes...")
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

    private fun updateCorpStat(code: String) {
        logger.info("Starting to update corporation status...")
        // 기업현황 크롤링
        val corpStat = naverFinanceCrawler.crawlCorpStat(code)
        // 이전 데이터
        val oldCorpStat = corpStatRepository.findByCode(code)
        if (oldCorpStat != null) {
            oldCorpStat.apply {
                this.code = corpStat.code
                this.financeSummaries = corpStat.financeSummaries
                this.updated = Instant.now()
            }
            corpStatRepository.save(oldCorpStat)
        } else {
            corpStatRepository.save(corpStat)
        }
    }

    private fun updateFinanceAnal(code: String) {
        logger.info("Starting to update finance analysis...")
        // 재무분석 크롤링
        val finAnal = naverFinanceCrawler.crawlFinanceAnal(code)
        val oldFinAnal = finAnalRepository.findByCode(code)
        if (oldFinAnal != null) {
            oldFinAnal.apply {
                this.code = finAnal.code
                financeStat = finAnal.financeStat
                updatedAt = Instant.now()
            }
            finAnalRepository.save(oldFinAnal)
        } else {
            finAnalRepository.save(finAnal)
        }
    }

    fun updateFinanceData() {
        logger.info("Starting to update finance data...")
        val excludes = naverFinanceCrawler.run { crawlEtfCodes() + crawlEtnCodes() }
        val tickers = tickerRepository.findAll().filter { !excludes.contains(it.code) }
        tickers.forEachIndexed { index, ticker ->
            logger.info("[${index + 1}/${tickers.size}] Starting to update finance data for ${ticker.code}")
            try {
                // 기업현황 업데이트
                updateCorpStat(ticker.code)
            } catch (e: Throwable) {
                logger.warn("Failed to update corpStat@${ticker.code} caused by ${e.javaClass.simpleName}:${e.message}")
            }

            try {
                // 재무분석 업데이트
                updateFinanceAnal(ticker.code)
            } catch (e: Throwable) {
                logger.warn("Failed to update financeAnal@${ticker.code} caused by ${e.javaClass.simpleName}:${e.message}")
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
                    ?.nearestFixed()
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