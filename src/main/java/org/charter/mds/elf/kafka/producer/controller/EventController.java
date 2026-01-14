
package org.charter.mds.elf.kafka.producer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.charter.mds.elf.kafka.producer.model.EventMessage;
import org.charter.mds.elf.kafka.producer.service.EventPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
@Tag(name = "Event Publisher", description = "APIs for publishing events to Kafka topics")
public class EventController {

    private final EventPublisherService publisherService;

    @PostMapping
    @Operation(summary = "Publish single event", description = "Publishes a single event to Kafka topic based on eventType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Event accepted for publishing"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> publishEvent(
            @Valid @RequestBody @Schema(description = "Event message to publish") EventMessage event) {
        log.info("Received event: eventType={}", event.getEventType());
        publisherService.publish(event);
        return ResponseEntity.accepted().body("Event accepted for publishing");
    }

    @PostMapping("/batch")
    @Operation(summary = "Publish batch events", description = "Publishes multiple events to Kafka topics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Events accepted for publishing"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> publishEvents(
            @Valid @RequestBody @Schema(description = "List of event messages to publish") List<EventMessage> events) {
        log.info("Received batch of {} events", events.size());
        publisherService.publishAll(events);
        return ResponseEntity.accepted().body(events.size() + " events accepted for publishing");
    }
}
