package com.example.kafka.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Schema(description = "Event message to be published to Kafka")
public class EventMessage {
    
    @NotBlank(message = "eventType is required")
    @JsonProperty("eventType")
    @Schema(description = "Type of the event (e.g., Event-1, Event-2)", example = "Event-1", required = true)
    private String eventType;
    
    @NotNull(message = "payload is required")
    @JsonProperty("payload")
    @Schema(description = "Event payload containing key-value pairs", example = "{\"key1\": \"value1\", \"timestamp\": \"2024-01-09T10:00:00Z\"}", required = true)
    private Map<String, Object> payload;
}
