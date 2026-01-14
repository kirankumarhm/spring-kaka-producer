
package org.charter.mds.elf.kafka.producer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.charter.mds.elf.kafka.producer.config.KafkaMetrics;
import org.charter.mds.elf.kafka.producer.config.TopicResolver;
import org.charter.mds.elf.kafka.producer.model.EventMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicResolver topicResolver;
    private final KafkaMetrics metrics;

    public void publish(EventMessage event) {
        List<String> topics = topicResolver.resolveTopics(event.getEventType());
        
        if (topics.contains("dead.events")) {
            metrics.getDeadLetterCounter().increment();
        }
        
        topics.forEach(topic -> send(topic, event));
    }

    public void publishAll(List<EventMessage> events) {
        events.forEach(this::publish);
    }

    private void send(String topic, EventMessage event) {
        long startTime = System.currentTimeMillis();
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);

        future.whenComplete((result, ex) -> {
            long duration = System.currentTimeMillis() - startTime;
            metrics.getPublishTimer().record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            if (ex == null) {
                metrics.getPublishSuccessCounter().increment();
                log.info("Event published | eventType={} | topic={} | partition={} | offset={} | duration={}ms",
                        event.getEventType(), topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        duration);
            } else {
                metrics.getPublishFailureCounter().increment();
                log.error("Publish failed | eventType={} | topic={} | duration={}ms", 
                        event.getEventType(), topic, duration, ex);
            }
        });
    }
}
