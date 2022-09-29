package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.parseDouble

fun main() {
    println(listOf("", null).parseAnd100MillionTimes())
}

private fun String?.parseAnd100MillionTimes(): Long? =
    this?.parseDouble()?.times(1_0000_0000)?.toLong()

private fun Collection<String?>.parseAnd100MillionTimes(): List<Long?> =
    this.map { it.parseAnd100MillionTimes() }