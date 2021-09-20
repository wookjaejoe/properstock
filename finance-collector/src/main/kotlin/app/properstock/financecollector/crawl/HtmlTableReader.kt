package app.properstock.financecollector.crawl

import org.jsoup.Jsoup

data class HtmlTableHeaders(
    val values: List<String>
)

data class HtmlTableRow(
    val values: List<String>
)

data class HtmlTable(
    val header: HtmlTableHeaders,
    val rows: List<HtmlTableRow>
) {
    fun get(index: Int, col: String): String = get(rows[index], col)
    fun get(row: HtmlTableRow, col: String): String = row.values[header.values.indexOf(col)]
}

class HtmlTableReader(val html: String) {
    fun read(): HtmlTable {
        val table = Jsoup.parse(html)
        val headers = HtmlTableHeaders(
            table
                .getElementsByTag("th")
                .map { it.text() }
        )
        val rows = table.getElementsByTag("tr")
            .filter { it.getElementsByTag("td").size > 0 && !it.text().isNullOrEmpty() }
            .map { it.getElementsByTag("td").map { th -> th.text() } }
            .map { HtmlTableRow(values = it) }

        return HtmlTable(headers, rows)
    }
}