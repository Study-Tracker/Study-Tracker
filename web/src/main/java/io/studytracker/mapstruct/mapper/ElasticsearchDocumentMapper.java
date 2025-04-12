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

package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.opensearch.OpensearchAssayDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchAssayTaskDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchAssayTypeDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchCollaboratorDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchCommentDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchConclusionsDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchFolderDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchKeywordDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchLinkDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchProgramDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchStudyDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchStudySummaryDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchUserDocument;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayType;
import io.studytracker.model.Collaborator;
import io.studytracker.model.Comment;
import io.studytracker.model.Conclusions;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Keyword;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ElasticsearchDocumentMapper {

  OpensearchStudyDocument fromStudy(Study study);

  List<OpensearchStudyDocument> fromStudyList(List<Study> studies);

  Set<OpensearchStudyDocument> fromStudySet(Set<Study> studies);

  OpensearchCollaboratorDocument fromCollaborator(Collaborator collaborator);

  List<OpensearchCollaboratorDocument> fromCollaboratorList(List<Collaborator> collaborators);

  Set<OpensearchCollaboratorDocument> fromCollaboratorSet(Set<Collaborator> collaborators);

  OpensearchCommentDocument fromCommentt(Comment comment);

  List<OpensearchCommentDocument> fromCommentList(List<Comment> comments);

  Set<OpensearchCommentDocument> fromCommentSet(Set<Comment> comments);

  OpensearchConclusionsDocument fromConclusions(Conclusions conclusions);

  OpensearchFolderDocument fromELNFolder(ELNFolder elnFolder);

  List<OpensearchFolderDocument> fromElnFolderList(List<ELNFolder> elnFolderList);

  Set<OpensearchFolderDocument> fromElnFolderSet(Set<ELNFolder> folders);

  OpensearchKeywordDocument fromKeyword(Keyword keyword);

  List<OpensearchKeywordDocument> fromKeywordList(List<Keyword> keywords);

  Set<OpensearchKeywordDocument> fromKeywordSet(Set<Keyword> keywords);

  OpensearchLinkDocument fromExternalLink(ExternalLink link);

  List<OpensearchLinkDocument> fromExternalLinkList(List<ExternalLink> links);

  Set<OpensearchLinkDocument> fromExternalLinkSet(Set<ExternalLink> links);

  OpensearchProgramDocument fromProgram(Program program);

  OpensearchUserDocument fromUser(User user);

  List<OpensearchUserDocument> fromUserList(List<User> users);

  Set<OpensearchUserDocument> fromUserSet(Set<User> users);

  OpensearchAssayDocument fromAssay(Assay assay);
  List<OpensearchAssayDocument> fromAssayList(List<Assay> assays);

  OpensearchAssayTypeDocument fromAssayType(AssayType assayType);

  OpensearchAssayTaskDocument fromAssayTask(AssayTask task);
  Set<OpensearchAssayTaskDocument> fromAssayTaskSet(Set<AssayTask> tasks);

  OpensearchStudySummaryDocument fromStudySummary(Study study);

  /** For mapping the {@link ExternalLink#getUrl()} field to a String * */
  default String map(URL url) {
    return url.toString();
  }
}
