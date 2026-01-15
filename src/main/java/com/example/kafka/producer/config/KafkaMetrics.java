
package com.example.kafka.producer.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaMetrics {

    private final Counter publishSuccessCounter;
    private final Counter publishFailureCounter;
    private final Counter deadLetterCounter;
    private final Timer publishTimer;

    public KafkaMetrics(MeterRegistry registry) {
        this.publishSuccessCounter = Counter.builder("kafka.publish.success")
                .description("Number of successful Kafka publishes")
                .register(registry);

        this.publishFailureCounter = Counter.builder("kafka.publish.failure")
                .description("Number of failed Kafka publishes")
                .register(registry);

        this.deadLetterCounter = Counter.builder("kafka.publish.deadletter")
                .description("Number of events routed to dead letter queue")
                .register(registry);

        this.publishTimer = Timer.builder("kafka.publish.duration")
                .description("Time taken to publish to Kafka")
                .register(registry);
    }
}
