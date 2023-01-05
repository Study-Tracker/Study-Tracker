package io.studytracker.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class HostInformation {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostInformation.class);

  @Value("${application.host-name:localhost}")
  private String hostName;

  @Value("${server.port}")
  private Integer port;

  @Value("${application.name}")
  private String applicationName;

  @Value("${application.java-version}")
  private String javaVersion;

  @Value("${application.version}")
  private String applicationVersion;

  @Value("${application.build-time}")
  private String buildTime;

  private String getProtocol() {
    return port.equals(443) || port.equals(8443) ? "https" : "http";
  }

  @JsonProperty("url")
  public String getApplicationUrl() {
    return String.format("%s://%s:%d", getProtocol(), hostName, port);
  }

  @PostConstruct
  public void init() {
    LOGGER.info("Host Information: " + this.toString());
  }

}
