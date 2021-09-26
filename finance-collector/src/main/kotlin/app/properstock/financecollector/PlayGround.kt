package app.properstock.financecollector


fun main() {
    println("[0-9]{4}/[0-9]{2}".toRegex().find("2017/12 (IFRS연결)")?.value)
}
