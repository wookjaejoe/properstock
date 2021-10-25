package app.properstock.financecollector.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
class SchedulerConfig : SchedulingConfigurer {
    override fun configureTasks(scheduledTaskRegistrar: ScheduledTaskRegistrar) {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 8
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-")
        threadPoolTaskScheduler.initialize()
        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
    }
}
