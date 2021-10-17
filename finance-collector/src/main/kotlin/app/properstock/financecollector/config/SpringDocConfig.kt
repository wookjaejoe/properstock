package app.properstock.financecollector.config

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springdoc.webflux.core.converters.WebFluxSupportConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringdocConfig {
    @Bean
    fun api(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        ModelConverters.getInstance().addConverter(WebFluxSupportConverter())
        return OpenAPI()
            .info(Info().title("Proper Stock Finance Collector").version("0.1"))
    }
}