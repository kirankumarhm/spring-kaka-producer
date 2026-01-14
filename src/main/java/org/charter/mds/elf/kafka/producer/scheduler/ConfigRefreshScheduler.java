
package org.charter.mds.elf.kafka.producer.scheduler;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class ConfigRefreshScheduler {

    private final ContextRefresher refresher;

    public ConfigRefreshScheduler(ContextRefresher refresher) {
        this.refresher = refresher;
    }

    // Runs every 1 minutes. You can also use a cron expression: @Scheduled(cron = "0 */1 * * * *")
    @Scheduled(fixedRateString = "${app.config.refresh-rate:60000}")
    public void autoRefresh() {
        // This triggers the logic to pull new data from Config Server
        Set<String> keys = refresher.refresh();
        
        if (!keys.isEmpty()) {
            System.out.println("Config refreshed! The following keys were updated: " + keys);
        }
    }
}
