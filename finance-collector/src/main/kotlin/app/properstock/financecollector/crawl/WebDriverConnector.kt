package app.properstock.financecollector.crawl

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Closeable
import java.net.URL
import java.util.concurrent.TimeUnit

@Component
class WebDriverConnector {
    @Value("\${webdriver.chrome.remote.url}")
    lateinit var chromeRemoteUrl: String

    private val logger: Logger = LoggerFactory.getLogger(WebDriverConnector::class.java)

    fun <R> connect(todo: CloseableWebDriver.() -> R): R {
        logger.info("Connecting with remote webdriver: $chromeRemoteUrl")
        return CloseableWebDriver(
            URL(chromeRemoteUrl),
            ChromeOptions().apply {
                this.setHeadless(true)
            }
        ).use {
            it.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS)
            it.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS)
            logger.debug("Remote webdriver connected.")
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
        override fun close() {
            quit()
        }
    }
}
