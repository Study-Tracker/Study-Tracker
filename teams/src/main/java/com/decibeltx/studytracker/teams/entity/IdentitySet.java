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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * { "application": {"@odata.type": "microsoft.graph.identity"}, "applicationInstance":
 * {"@odata.type": "microsoft.graph.identity"}, "conversation": {"@odata.type":
 * "microsoft.graph.identity"}, "conversationIdentityType": {"@odata.type":
 * "microsoft.graph.identity"}, "device": {"@odata.type": "microsoft.graph.identity"}, "encrypted":
 * {"@odata.type": "microsoft.graph.identity"}, "guest": {"@odata.type":
 * "microsoft.graph.identity"}, "phone": {"@odata.type": "microsoft.graph.identity"}, "user":
 * {"@odata.type": "microsoft.graph.identity"} }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentitySet {

  private Identity application;
  private Identity applicationInstance;
  private Identity conversation;
  private Identity conversationIdentityType;
  private Identity device;
  private Identity encrypted;
  private Identity guest;
  private Identity phone;
  private Identity user;

}
