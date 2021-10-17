package app.properstock.financecollector.config

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChromeDriverConfig {

    @Value("\${chromedriver}")
    private lateinit var chromedriver: String

    @Bean
    fun getChromeDriver(): ChromeDriver {
        System.setProperty("webdriver.chrome.driver", chromedriver)
        val chromeOptions = ChromeOptions()
        chromeOptions.setHeadless(true)
        return ChromeDriver(chromeOptions)
    }
}