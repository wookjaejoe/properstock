package app.properstock.financecollector

import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL

class ChromeAgentConnectTest {
    @Test
    fun testConnect() {
        val driver = RemoteWebDriver(
            URL("http://127.0.0.1:4444"),
            ChromeOptions()
        )
        driver.get("https://www.google.com")
        val outerHtml = driver.findElement(By.tagName("html"))
            .getAttribute("outerHTML")

        println(outerHtml)
        driver.quit()
    }
}