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

package com.decibeltx.studytracker.elasticsearch;

import com.decibeltx.studytracker.core.keyword.Keyword;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElasticsearchKeywordDeserializer extends StdDeserializer<KeywordHits> {

  public ElasticsearchKeywordDeserializer(Class<?> vc) {
    super(vc);
  }

  public ElasticsearchKeywordDeserializer() {
    this(null);
  }

  @Override
  public KeywordHits deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext)
      throws IOException, JsonProcessingException {
    KeywordHits keywordHits = new KeywordHits();
    List<Keyword> keywords = new ArrayList<>();
    JsonNode root = jsonParser.getCodec().readTree(jsonParser);
    if (root.has("hits")) {
      JsonNode hitObj = root.get("hits");
      keywordHits.setNumHits(hitObj.get("total").asLong());
      keywordHits.setMaxScore(hitObj.get("max_score").asDouble());
      if (hitObj.has("hits") && hitObj.get("hits").isArray()) {
        for (JsonNode hit : hitObj.get("hits")) {
          Optional<Keyword> optional = getKeywordFromHit(hit);
          if (optional.isPresent()) {
            keywords.add(optional.get());
          }
        }
      }
      keywordHits.setHits(keywords);
    }
    return keywordHits;
  }

  private Optional<Keyword> getKeywordFromHit(JsonNode hit) {
    Keyword keyword = null;
    if (hit.has("_id") && hit.has("_index") && hit.has("_source") && hit.get("_source")
        .has("doc")) {
      keyword = new Keyword();
      keyword.setReferenceId(hit.get("_id").asText());
      keyword.setType(hit.get("_index").asText());
      keyword.setKeyword(hit.get("_source").get("doc").get("name").asText());
    }
    return Optional.ofNullable(keyword);
  }

}
