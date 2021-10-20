package app.properstock.financecollector.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URL


// fixme: 세션을 계속 쥐고 있으면 안되고 쓸데만 연결해서 쓰고 다쓰면 연결 끊어야 할 듯
@Configuration
class ChromeDriverConfig {

    @Value("\${webdriver.chrome.remote.url}")
    private lateinit var chromeRemoteUrl: String

    @Bean
    fun getChromeDriver(): WebDriver {
        logger.info("Connecting with remote webdriver: $chromeRemoteUrl")
        return RemoteWebDriver(
            URL(chromeRemoteUrl),
            ChromeOptions()
        ).apply {
            logger.info("Remote webdriver connected.")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ChromeDriverConfig::class.java)
    }
}