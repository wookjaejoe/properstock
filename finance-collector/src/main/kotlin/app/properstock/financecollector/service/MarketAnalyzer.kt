package app.properstock.financecollector.service

import app.properstock.financecollector.TZ_KR
import app.properstock.financecollector.repository.TickerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MarketAnalyzer(
    val tickerRepository: TickerRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(MarketAnalyzer::class.java)

    final var avgOfPer: Double = avgOfPer()

    private final fun avgOfPer(): Double =
        tickerRepository.findAll().mapNotNull { it.per }.filter { !it.isNaN() }.average()

    @Scheduled(cron = "0 * * * * *", zone = TZ_KR)
    fun update() {
        logger.info("${MarketAnalyzer::class.simpleName} - update()")
        this.avgOfPer = avgOfPer()
    }
}