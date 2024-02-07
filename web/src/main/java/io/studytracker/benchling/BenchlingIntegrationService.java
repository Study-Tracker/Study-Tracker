package io.studytracker.benchling;

import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.BenchlingIntegration;
import io.studytracker.repository.BenchlingIntegrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class BenchlingIntegrationService implements IntegrationService<BenchlingIntegration> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingIntegrationService.class);
    
    @Autowired
    private BenchlingIntegrationRepository benchlingIntegrationRepository;
    
    @Autowired
    private BenchlingClientFactory benchlingClientFactory;
    
    @Override
    public Optional<BenchlingIntegration> findById(Long id) {
        LOGGER.debug("Find BenchlingIntegration by id: {}", id);
        return benchlingIntegrationRepository.findById(id);
    }
    
    @Override
    public List<BenchlingIntegration> findAll() {
        LOGGER.debug("Find all BenchlingIntegration");
        return benchlingIntegrationRepository.findAll();
    }
    
    @Override
    @Transactional
    public BenchlingIntegration register(BenchlingIntegration instance) {
        LOGGER.info("Registering BenchlingIntegration");
        if (!validate(instance)) {
            throw new IllegalArgumentException("One or more required fields are missing.");
        }
        if (!test(instance)) {
            throw new IllegalArgumentException("Failed to connect to Benchling API with the provided credentials.");
        }
        instance.setActive(true);
        return benchlingIntegrationRepository.save(instance);
    }
    
    @Override
    @Transactional
    public BenchlingIntegration update(BenchlingIntegration instance) {
        LOGGER.info("Updating BenchlingIntegration: {}", instance.getId());
        if (!validate(instance)) {
            throw new IllegalArgumentException("One or more required fields are missing.");
        }
        if (!test(instance)) {
            throw new IllegalArgumentException("Failed to connect to Benchling API with the provided credentials.");
        }
        BenchlingIntegration i = benchlingIntegrationRepository.getById(instance.getId());
        i.setName(instance.getName());
        i.setTenantName(instance.getTenantName());
        i.setRootUrl(instance.getRootUrl());
        i.setClientId(instance.getClientId());
        i.setClientSecret(instance.getClientSecret());
        i.setUsername(instance.getUsername());
        i.setPassword(instance.getPassword());
        i.setActive(instance.isActive());
        return benchlingIntegrationRepository.save(i);
    }
    
    @Override
    public boolean validate(BenchlingIntegration instance) {
        try {
            Assert.hasText(instance.getTenantName(), "Tenant name is required.");
            Assert.hasText(instance.getRootUrl(), "Root URL is required.");
            Assert.hasText(instance.getClientId(), "Client ID is required.");
            Assert.hasText(instance.getClientSecret(), "Client secret is required.");
        } catch (Exception e) {
            LOGGER.warn("Benchling Integration validation failed: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    public boolean test(BenchlingIntegration instance) {
        try {
            BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(instance);
            BenchlingEntryTemplateList list = client.findEntryTemplates(null);
            Assert.notNull(list, "Failed to connect to Benchling API with the provided credentials.");
        } catch (Exception e) {
            LOGGER.warn("Benchling Integration test failed: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    @Transactional
    public void remove(BenchlingIntegration instance) {
        LOGGER.info("Removing BenchlingIntegration: {}", instance.getId());
        BenchlingIntegration i = benchlingIntegrationRepository.getById(instance.getId());
        i.setActive(false);
        benchlingIntegrationRepository.save(i);
    }
}
