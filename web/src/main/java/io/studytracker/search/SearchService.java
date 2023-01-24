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

import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import java.util.Collection;

public interface SearchService {

  GenericSearchHits<? extends PowerSearchDocument<?>> search(String keyword);

  GenericSearchHits<? extends PowerSearchDocument<?>> search(String keyword, String field);

  GenericSearchHits<? extends StudySearchDocument<?>> searchStudies(String keyword);
  GenericSearchHits<? extends StudySearchDocument<?>> searchStudies(String keyword, String field);

  GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword);
  GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword, String field);

  void indexStudy(Study study);

  void indexStudies(Collection<Study> studies);

  void indexAssay(Assay assay);

  void indexAssays(Collection<Assay> assays);
}
