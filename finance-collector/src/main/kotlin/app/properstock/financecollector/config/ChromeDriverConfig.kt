package app.properstock.financecollector.config

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChromeDriverConfig {
    @Bean
    fun driver(): ChromeDriver {
        System.setProperty(
            "webdriver.chrome.driver",
            """/Users/wjjo/chromedriver-93-0-4577-63"""
        )
        val chromeOptions = ChromeOptions()
        chromeOptions.setHeadless(true)
        return ChromeDriver(chromeOptions)
    }
}