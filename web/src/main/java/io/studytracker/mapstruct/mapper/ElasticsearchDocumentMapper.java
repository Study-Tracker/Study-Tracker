/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchAssayDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchAssayTaskDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchAssayTypeDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchCollaboratorDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchCommentDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchConclusionsDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchFolderDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchKeywordDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchLinkDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchProgramDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudySummaryDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchUserDocument;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayType;
import io.studytracker.model.Collaborator;
import io.studytracker.model.Comment;
import io.studytracker.model.Conclusions;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.model.Keyword;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElasticsearchDocumentMapper {

  ElasticsearchStudyDocument fromStudy(Study study);

  List<ElasticsearchStudyDocument> fromStudyList(List<Study> studies);

  Set<ElasticsearchStudyDocument> fromStudySet(Set<Study> studies);

  ElasticsearchCollaboratorDocument fromCollaborator(Collaborator collaborator);

  List<ElasticsearchCollaboratorDocument> fromCollaboratorList(List<Collaborator> collaborators);

  Set<ElasticsearchCollaboratorDocument> fromCollaboratorSet(Set<Collaborator> collaborators);

  ElasticsearchCommentDocument fromCommentt(Comment comment);

  List<ElasticsearchCommentDocument> fromCommentList(List<Comment> comments);

  Set<ElasticsearchCommentDocument> fromCommentSet(Set<Comment> comments);

  ElasticsearchConclusionsDocument fromConclusions(Conclusions conclusions);

  ElasticsearchFolderDocument fromELNFolder(ELNFolder elnFolder);

  List<ElasticsearchFolderDocument> fromElnFolderList(List<ELNFolder> elnFolderList);

  Set<ElasticsearchFolderDocument> fromElnFolderSet(Set<ELNFolder> folders);

  ElasticsearchFolderDocument fromFileStoreFolder(FileStoreFolder fileStoreFolder);

  List<ElasticsearchFolderDocument> fromFileStoreFolderList(List<FileStoreFolder> folders);

  Set<ElasticsearchFolderDocument> fromFileStoreFolderSet(Set<FileStoreFolder> folders);

  @Mapping(target = "category", source="category.name")
  ElasticsearchKeywordDocument fromKeyword(Keyword keyword);

  List<ElasticsearchKeywordDocument> fromKeywordList(List<Keyword> keywords);

  Set<ElasticsearchKeywordDocument> fromKeywordSet(Set<Keyword> keywords);

  ElasticsearchLinkDocument fromExternalLink(ExternalLink link);

  List<ElasticsearchLinkDocument> fromExternalLinkList(List<ExternalLink> links);

  Set<ElasticsearchLinkDocument> fromExternalLinkSet(Set<ExternalLink> links);

  ElasticsearchProgramDocument fromProgram(Program program);

  ElasticsearchUserDocument fromUser(User user);

  List<ElasticsearchUserDocument> fromUserList(List<User> users);

  Set<ElasticsearchUserDocument> fromUserSet(Set<User> users);

  ElasticsearchAssayDocument fromAssay(Assay assay);
  List<ElasticsearchAssayDocument> fromAssayList(List<Assay> assays);

  ElasticsearchAssayTypeDocument fromAssayType(AssayType assayType);

  ElasticsearchAssayTaskDocument fromAssayTask(AssayTask task);
  Set<ElasticsearchAssayTaskDocument> fromAssayTaskSet(Set<AssayTask> tasks);

  ElasticsearchStudySummaryDocument fromStudySummary(Study study);

  /** For mapping the {@link ExternalLink#getUrl()} field to a String * */
  default String map(URL url) {
    return url.toString();
  }
}
