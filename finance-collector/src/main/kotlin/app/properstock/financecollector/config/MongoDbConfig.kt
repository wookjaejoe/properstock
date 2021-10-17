package app.properstock.financecollector.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.YearMonth

@Configuration
class MongoDbConfig

@Configuration
class MongoDbConversions {
    @Bean
    fun customConversion(): MongoCustomConversions = MongoCustomConversions(
        listOf(
            YearMonthToStringConverter(),
            StringToYearMonthConverter()
        )
    )
}

@WritingConverter
class YearMonthToStringConverter : Converter<YearMonth, String> {
    override fun convert(source: YearMonth): String = source.toString()
}

@ReadingConverter
class StringToYearMonthConverter : Converter<String, YearMonth> {
    override fun convert(source: String): YearMonth = YearMonth.parse(source)
}