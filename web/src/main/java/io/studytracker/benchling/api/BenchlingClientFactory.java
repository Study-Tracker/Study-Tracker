package io.studytracker.benchling.api;

import io.studytracker.model.BenchlingIntegration;
import org.springframework.web.client.RestTemplate;

public class BenchlingClientFactory {
    
    private final RestTemplate benchlingRestTemplate;
    
    public BenchlingClientFactory(RestTemplate benchlingRestTemplate) {
        this.benchlingRestTemplate = benchlingRestTemplate;
    }
    
    public BenchlingElnRestClient createBenchlingClient(BenchlingIntegration integration) {
        return new BenchlingElnRestClient(benchlingRestTemplate, integration);
    }
}
