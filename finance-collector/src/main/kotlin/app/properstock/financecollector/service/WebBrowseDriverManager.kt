package app.properstock.financecollector.service

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service

@Service
class WebBrowseDriverManager {
    fun default(): ChromeDriver {
        System.setProperty(
            "webdriver.chrome.driver",
            """/Users/wjjo/chromedriver-93-0-4577-63"""
        )
        val chromeOptions = ChromeOptions()
        chromeOptions.setHeadless(true)
        return ChromeDriver(chromeOptions)
    }
}