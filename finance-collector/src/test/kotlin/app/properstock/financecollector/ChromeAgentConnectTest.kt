package app.properstock.financecollector

import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL

fun main() {
    print("Connecting with driver...")
    val driver = RemoteWebDriver(
        URL("http://localhost:4444"),
        ChromeOptions()
    )
    println("Driver connected.")

    val testUrl = "https://www.google.com"
    println("Crawling test page...")
    driver.get(testUrl)
    val outerHtml = driver.findElement(By.tagName("html"))
        .getAttribute("outerHTML")

    println(outerHtml)
    driver.quit()
}
