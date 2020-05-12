/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.teams.entity;

import com.decibeltx.studytracker.core.model.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.Date;
import lombok.Data;

/**
 * { "id": "string (identifier)", "replyToId": "string (identifier)", "from": {"@odata.type":
 * "microsoft.graph.identitySet"}, "etag": "string", "messageType": "string", "createdDateTime":
 * "string (timestamp)", "lastModifiedDateTime": "string (timestamp)", "deletedDateTime": "string
 * (timestamp)", "subject": "string", "body": {"@odata.type": "microsoft.graph.itemBody"},
 * "summary": "string", "attachments": [{"@odata.type": "microsoft.graph.chatMessageAttachment"}],
 * "mentions": [{"@odata.type": "microsoft.graph.chatMessageMention"}], "importance": "string",
 * "policyViolation": "string", "reactions": [{"@odata.type": "microsoft.graph.chatMessageReaction"}],
 * "locale": "string", "deleted": true }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage implements Message {

  private String id;
  private String replyToId;
  private IdentitySet from;
  private String etag;
  private String messageType;
  private Date createdDateTime;
  private Date lastModifiedDateTime;
  private Date deletedDateTime;
  private String subject;
  @JsonProperty("body")
  private ItemBody itemBody;
  private String summary;
  private Object attachments;
  private Object mentions;
  private String importance;
  private String privacyViolation;
  private Object reactions;
  private String locale;
  private boolean deleted;
  private URL webUrl;

  @Override
  @JsonIgnore
  public URL getUrl() {
    return webUrl;
  }

  @Override
  @JsonIgnore
  public String getBody() {
    return itemBody.getContent();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
  static class ItemBody {

    @JsonProperty("contentType")
    private String conentType;

    @JsonProperty("content")
    private String content;

  }

}
