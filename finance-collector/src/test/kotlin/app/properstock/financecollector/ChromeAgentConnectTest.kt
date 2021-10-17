package app.properstock.financecollector

import org.junit.jupiter.api.Test
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
        driver.quit()
    }
}