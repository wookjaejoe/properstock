package app.properstock.financecollector.crawl.nf

data class StringMap(
    private val ignoreCase: Boolean = true,
    private val ignoreWhiteSpace: Boolean = true
) : LinkedHashMap<String, String>() {
    private fun normalize(key: String): String {
        var result: String = key
        if (ignoreCase) result = result.uppercase()
        if (ignoreWhiteSpace) result = result.replace(" ", "")
        return result
    }

    fun find(key: String): String? {
        return this[this.keys.find { normalize(key).contentEquals(normalize(it)) }]
    }
}