package app.properstock.financecollector.crawl

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Closeable
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

@Component
class WebDriverConnector {
    @Value("\${webdriver.chrome.remote.url}")
    lateinit var chromeRemoteUrl: String

    fun <R> connect(todo: CloseableWebDriver.() -> R): R {
        logger.info("Connecting with remote webdriver: $chromeRemoteUrl")
        return CloseableWebDriver(
            URL(chromeRemoteUrl),
            ChromeOptions()
        ).use {
            logger.info("Remote webdriver connected.")
            todo(it)
        }
    }

    class CloseableWebDriver(
        url: URL,
        chromeOptions: ChromeOptions
    ) : Closeable, RemoteWebDriver(
        url,
        chromeOptions
    ) {
        val autoCloser = Timer().schedule(
            30 * 1000  // 30초
        ) {
            logger.warn("The connection has not been closed for 30 seconds, force close.")
            close()
        }

        override fun close() {
            autoCloser.cancel()
            quit()
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(WebDriverConnector::class.java)
    }
}
