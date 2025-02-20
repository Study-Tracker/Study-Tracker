/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.benchling;

import io.studytracker.benchling.api.BenchlingElnRestClient;
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
