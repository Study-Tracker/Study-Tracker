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

package io.studytracker.controller.api.internal;

import io.studytracker.benchling.BenchlingIntegrationService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.BenchlingIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.BenchlingIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.BenchlingIntegrationMapper;
import io.studytracker.model.BenchlingIntegration;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/integrations/benchling")
public class BenchlingIntegrationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingIntegrationController.class);
    
    @Autowired
    private BenchlingIntegrationService benchlingIntegrationService;
    
    @Autowired
    private BenchlingIntegrationMapper benchlingIntegrationMapper;
    
    @GetMapping("")
    public List<BenchlingIntegrationDetailsDto> fetchIntegrations() {
        LOGGER.debug("Fetching Benchling integrations");
        return benchlingIntegrationMapper.toDetailsDto(benchlingIntegrationService.findAll());
    }
    
    @PostMapping("")
    public HttpEntity<BenchlingIntegrationDetailsDto> registerIntegration(@Valid @RequestBody BenchlingIntegrationFormDto dto) {
        LOGGER.info("Registering Benchling integration: {}", dto.getName());
        BenchlingIntegration integration = benchlingIntegrationMapper.fromFormDto(dto);
        BenchlingIntegration created = benchlingIntegrationService.register(integration);
        return new ResponseEntity<>(benchlingIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public HttpEntity<BenchlingIntegrationDetailsDto> updateIntegration(@PathVariable("id") Long id,
            @Valid @RequestBody BenchlingIntegrationFormDto dto) {
        LOGGER.info("Updating Benchling integration {}", id);
        BenchlingIntegration integration = benchlingIntegrationMapper.fromFormDto(dto);
        BenchlingIntegration updated = benchlingIntegrationService.update(integration);
        return new ResponseEntity<>(benchlingIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
    }
    
    @PatchMapping("/{id}")
    public HttpEntity<?> toggleIntegrationStatus(@RequestParam("active") boolean active,
            @PathVariable("id") Long id) {
        LOGGER.info("Updating Benchling integration {} status to {}", id, active);
        Optional<BenchlingIntegration> optional = benchlingIntegrationService.findById(id);
        if (optional.isEmpty()) {
            throw new RecordNotFoundException("AWS integration not found");
        }
        BenchlingIntegration integration = optional.get();
        integration.setActive(active);
        benchlingIntegrationService.update(integration);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteIntegration(@PathVariable("id") Long id) {
        LOGGER.info("Deleting Benchling integration {}", id);
        Optional<BenchlingIntegration> optional = benchlingIntegrationService.findById(id);
        if (optional.isEmpty()) {
            throw new RecordNotFoundException("Benchling integration not found");
        }
        benchlingIntegrationService.remove(optional.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
