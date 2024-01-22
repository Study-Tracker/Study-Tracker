package io.studytracker.mapstruct.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class BenchlingIntegrationDetailsDto {
    
    private Long id;
    private String name;
    private String tenantName;
    private String rootUrl;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;
    
}
