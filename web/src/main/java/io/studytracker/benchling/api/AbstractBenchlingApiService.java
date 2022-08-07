package io.studytracker.benchling.api;

import io.studytracker.benchling.api.entities.BenchlingAuthenticationToken;
import io.studytracker.benchling.api.entities.BenchlingEntry;
import io.studytracker.eln.NotebookEntry;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public abstract class AbstractBenchlingApiService {

  @Autowired
  private BenchlingElnRestClient client;

  @Value("${benchling.tenant-name}")
  private String tenantName;

  @Value("${benchling.api.username:}")
  private String username;

  @Value("${benchling.api.password:}")
  private String password;

  @Value("${benchling.api.client-id:}")
  private String clientId;

  @Value("${benchling.api.client-secret:}")
  private String clientSecret;

  @Value("${benchling.api.token:}")
  private String token;

  private URL rootUrl;

  private URL rootFolderUrl;

  @PostConstruct
  public void init() throws MalformedURLException {
    rootUrl = new URL("https://" + tenantName + ".benchling.com");
    rootFolderUrl = new URL(rootUrl, "/" + tenantName + "/f_");
  }

  /**
   * Generates an Authorization header to be used in REST API requests. The header will acquired
   * based on the provided configuration. If an application client is provided, a Bearer token will
   * be generated. Otherwise, a HTTP Basic auth header will be used.
   *
   * @return token
   */
  protected String generateAuthorizationHeader() {
    if (StringUtils.hasText(token)) {
      return "Basic " + token;
    } else if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
      String auth = username + ":" + password;
      byte[] bytes = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
      return "Basic " + new String(bytes);
    } else {
      BenchlingAuthenticationToken benchlingAuthenticationToken =
          client.acquireApplicationAuthenticationToken(clientId, clientSecret);
      String token = benchlingAuthenticationToken.getAccessToken();
      return "Bearer " + token;
    }
  }

  /**
   * Converts a {@link BenchlingEntry} object into a {@link NotebookEntry} object.
   *
   * @param benchlingEntry
   * @return
   */
  protected NotebookEntry convertBenchlingEntry(BenchlingEntry benchlingEntry) {
    NotebookEntry notebookEntry = new NotebookEntry();
    notebookEntry.setName(benchlingEntry.getName());
    notebookEntry.setReferenceId(benchlingEntry.getId());
    notebookEntry.setUrl(benchlingEntry.getWebURL());
    notebookEntry.getAttributes().put("folderId", benchlingEntry.getFolderId());
    return notebookEntry;
  }

  public BenchlingElnRestClient getClient() {
    return client;
  }

  public URL getRootUrl() {
    return rootUrl;
  }

  public URL getRootFolderUrl() {
    return rootFolderUrl;
  }
}
