package app.properstock.financecollector.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URL

@Configuration
class ChromeDriverConfig {

//    @Value("\${chromedriver}")
//    private lateinit var chromedriver: String

    @Bean
    fun getChromeDriver(): WebDriver {
        return RemoteWebDriver(
            URL("http://127.0.0.1:4444"),
            ChromeOptions()
        )
    }
}