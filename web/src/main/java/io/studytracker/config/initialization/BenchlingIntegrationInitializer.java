package io.studytracker.config.initialization;

import io.studytracker.benchling.BenchlingIntegrationService;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.model.BenchlingIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class BenchlingIntegrationInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingIntegrationInitializer.class);
    
    @Value("${benchling.tenant-name:}")
    private String tenantName;
    
    @Value("${benchling.root-url:}")
    private String rootUrl;
    
    @Value("${benchling.api.client-id:}")
    private String clientId;
    
    @Value("${benchling.api.client-secret:}")
    private String clientSecret;
    
    @Autowired
    private BenchlingIntegrationService benchlingIntegrationService;
    
    @Transactional
    public void initializeIntegrations() throws InvalidConfigurationException {
        
        // Register the Benchling integration
        LOGGER.info("Checking Benchling integration status...");
        
        if (StringUtils.hasText(tenantName) && StringUtils.hasText(rootUrl)
                && StringUtils.hasText(clientId) && StringUtils.hasText(clientSecret)) {
            
            // Get existing integrations
            List<BenchlingIntegration> integrations = benchlingIntegrationService.findAll();
            
            // If integrations exist, skip initialization
            if (!integrations.isEmpty()) {
                LOGGER.info("Benchling integration already registered.");
            }
            
            // If no integrations exist, register a new one
            else {
                LOGGER.info("Registering Benchling integration...");
                try {
                    BenchlingIntegration integration = new BenchlingIntegration();
                    integration.setName("Benchling");
                    integration.setTenantName(tenantName);
                    integration.setRootUrl(rootUrl);
                    integration.setClientId(clientId);
                    integration.setClientSecret(clientSecret);
                    benchlingIntegrationService.register(integration);
                } catch (Exception e) {
                    LOGGER.error("Failed to register Benchling integration.", e);
                }
            }
            
        } else {
            LOGGER.info("Benchling integration properties not present. Skipping initialization.");
        }
    }
    
}
