
package com.example.kafka.producer.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "kafka")
public class TopicResolver {

    private Map<String, List<String>> topicRouting;
    private Map<String, List<String>> routingSnapshot;

    @PostConstruct
    void init() {
        this.routingSnapshot = topicRouting != null
                ? Map.copyOf(topicRouting)
                : Collections.emptyMap();
        log.info("TopicResolver initialized with routes: {}", routingSnapshot);
    }

    public List<String> resolveTopics(String eventType) {
        List<String> topics = routingSnapshot.get(eventType);

        if (topics == null || topics.isEmpty()) {
            log.warn("No topic mapping for eventType={}, routing to dead.events", eventType);
            return List.of("dead.events");
        }

        return topics;
    }
}
