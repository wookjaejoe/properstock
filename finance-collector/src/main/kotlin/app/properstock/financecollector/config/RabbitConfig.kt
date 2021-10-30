package app.properstock.financecollector.config

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("currentPrice")
    }

    @Bean
    fun currentPrice(): Queue {
        return AnonymousQueue()
    }

    @Bean
    fun bind(
        fanout: FanoutExchange,
        currentPrice: Queue
    ): Binding {
        return BindingBuilder.bind(currentPrice).to(fanout)
    }
}
