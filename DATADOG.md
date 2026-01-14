# Datadog Monitoring Guide

## Setup

### 1. Environment Variables
```bash
export DATADOG_API_KEY=your-api-key
export DATADOG_APP_KEY=your-app-key
export SPRING_PROFILES_ACTIVE=prod
```

### 2. Datadog Agent (Optional)
For enhanced monitoring, install Datadog Agent:
```bash
DD_API_KEY=your-api-key DD_SITE="datadoghq.com" bash -c "$(curl -L https://s3.amazonaws.com/dd-agent/scripts/install_script.sh)"
```

## Custom Metrics

All metrics are automatically exported to Datadog:

### Application Metrics
- `kafka.publish.success` - Successful Kafka publishes
- `kafka.publish.failure` - Failed Kafka publishes
- `kafka.publish.deadletter` - Dead letter queue events
- `kafka.publish.duration` - Publish latency (ms)

### JVM Metrics
- `jvm.memory.used`
- `jvm.memory.max`
- `jvm.gc.pause`
- `jvm.threads.live`

### HTTP Metrics
- `http.server.requests` - Request count and latency
- Tagged by: endpoint, status, method

## Datadog Dashboards

### Create Dashboard
1. Go to Datadog → Dashboards → New Dashboard
2. Add widgets for key metrics

### Recommended Widgets

**1. Publish Success Rate**
```
sum:kafka.publish.success{application:kafka-producer}.as_rate()
```

**2. Publish Failure Rate**
```
sum:kafka.publish.failure{application:kafka-producer}.as_rate()
```

**3. Dead Letter Queue Rate**
```
sum:kafka.publish.deadletter{application:kafka-producer}.as_rate()
```

**4. Average Publish Latency**
```
avg:kafka.publish.duration{application:kafka-producer}
```

**5. P99 Latency**
```
p99:kafka.publish.duration{application:kafka-producer}
```

**6. JVM Memory Usage**
```
avg:jvm.memory.used{application:kafka-producer}
```

## Datadog Monitors (Alerts)

### 1. High Failure Rate
```
Metric: kafka.publish.failure
Condition: sum(last_5m) > 100
Alert: Critical
Message: Kafka publish failure rate is high
```

### 2. High Dead Letter Rate
```
Metric: kafka.publish.deadletter
Condition: sum(last_5m) > 50
Alert: Warning
Message: High number of events routed to dead letter queue
```

### 3. High Latency
```
Metric: kafka.publish.duration
Condition: p99(last_5m) > 1000
Alert: Warning
Message: Kafka publish latency is high (>1s)
```

### 4. Service Down
```
Metric: http.server.requests
Condition: no data for 5m
Alert: Critical
Message: Kafka producer service is down
```

## APM (Application Performance Monitoring)

### Enable Datadog APM
Add to startup:
```bash
java -javaagent:/path/to/dd-java-agent.jar \
  -Ddd.service=kafka-producer \
  -Ddd.env=production \
  -Ddd.version=1.0.0 \
  -Ddd.trace.enabled=true \
  -jar app.jar
```

### Kubernetes Deployment
```yaml
env:
  - name: DD_AGENT_HOST
    valueFrom:
      fieldRef:
        fieldPath: status.hostIP
  - name: DD_SERVICE
    value: "kafka-producer"
  - name: DD_ENV
    value: "production"
  - name: DD_TRACE_ENABLED
    value: "true"
```

## Log Management

### Configure Log Collection
```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  level:
    org.charter.mds.elf.kafka.producer: INFO
```

### Datadog Log Queries
```
service:kafka-producer status:error
service:kafka-producer "Publish failed"
service:kafka-producer "dead.events"
service:kafka-producer @duration:>1000
```

## Service Map

Datadog APM automatically creates service maps showing:
- Kafka Producer → Kafka Broker
- Config Server → Kafka Producer
- Request flow and dependencies

## SLIs/SLOs

### Recommended SLOs

**Availability**: 99.9%
```
sum:http.server.requests{status:2xx,application:kafka-producer} / 
sum:http.server.requests{application:kafka-producer}
```

**Latency**: P99 < 500ms
```
p99:kafka.publish.duration{application:kafka-producer} < 500
```

**Error Rate**: < 0.1%
```
sum:kafka.publish.failure{application:kafka-producer} / 
sum:kafka.publish.success{application:kafka-producer} < 0.001
```

## Tags

All metrics are tagged with:
- `application:kafka-producer`
- `environment:dev|prod`
- `host:hostname`
- `version:app-version`

Use tags for filtering and grouping in Datadog.
