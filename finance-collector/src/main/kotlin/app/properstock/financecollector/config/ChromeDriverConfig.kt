package app.properstock.financecollector.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URL

@Configuration
class ChromeDriverConfig {

    @Value("\${webdriver.chrome.remote.url}")
    private lateinit var chromeRemoteUrl: String

    @Bean
    fun getChromeDriver(): WebDriver {
        return RemoteWebDriver(
            URL(chromeRemoteUrl),
            ChromeOptions()
        )
    }
}