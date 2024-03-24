package app.properstock.financecollector.util

import java.net.HttpURLConnection
import java.net.URL

object HttpSimpleRequester {

    fun get(url: URL) =
        url
            .openConnection()
            .let { it as HttpURLConnection }
            .also { if (it.responseCode / 100 != 2) throw RuntimeException("Request failure: GET $url") }
            .inputStream
            .bufferedReader()
            .use { it.readLines().joinToString(System.lineSeparator()) }
}
