package io.studytracker.controller.api.internal;

import io.studytracker.benchling.BenchlingIntegrationService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.BenchlingIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.BenchlingIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.BenchlingIntegrationMapper;
import io.studytracker.model.BenchlingIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
