package io.studytracker.mapstruct.dto.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BenchlingIntegrationFormDto {
    private Long id;
    private @NotBlank String name;
    private @NotBlank String tenantName;
    private String rootUrl;
    private @NotBlank String clientId;
    private @NotBlank String clientSecret;
    private boolean active;
}
