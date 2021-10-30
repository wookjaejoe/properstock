package app.properstock.financecollector.service

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class CurrentPriceListener {
    @RabbitListener(queues = ["#{currentPrice.name}"])
    fun receive(message: String) {
        // todo: update current price
    }
}