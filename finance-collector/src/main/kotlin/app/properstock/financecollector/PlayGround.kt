package app.properstock.financecollector


fun main() {
    println("(?<=code=)[A-Za-z0-9]+".toRegex().find("code=123123&")?.value)
}
