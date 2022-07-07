package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthFeaturesDto {

  private SingleSignOnOptionsDto sso = new SingleSignOnOptionsDto();

  @JsonProperty("logoutUrl")
  public String getLogoutUrl() {
    if (sso.isEnabled()) {
      return "/saml/logout";
    } else {
      return "/logout";
    }
  }

  @Data
  public static class SingleSignOnOptionsDto {

    private String mode;
    private String ssoUrl;

    @JsonProperty("isEnabled")
    public boolean isEnabled() {
      return mode.equals("okta-saml");
    }

  }

}


