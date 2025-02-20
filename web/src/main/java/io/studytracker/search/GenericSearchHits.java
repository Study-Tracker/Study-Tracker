/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.search;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

@Data
public class GenericSearchHits<T extends SearchDocument<?>> {

  private Long numHits;
  private Float maxScore;
  private List<GenericSearchHit<T>> hits = new ArrayList<>();

  public static <S extends SearchDocument<?>> GenericSearchHits<S> fromElasticsearchHits(
      SearchHits<S> searchHits) {
    GenericSearchHits<S> genericHits = new GenericSearchHits<>();
    genericHits.setNumHits(searchHits.getTotalHits());
    genericHits.setMaxScore(searchHits.getMaxScore());
    List<GenericSearchHit<S>> hits = new ArrayList<>();
    for (SearchHit<S> searchHit : searchHits.getSearchHits()) {
      hits.add(GenericSearchHit.fromElasticsearchSearchHit(searchHit));
    }
    genericHits.setHits(hits);
    return genericHits;
  }
}
