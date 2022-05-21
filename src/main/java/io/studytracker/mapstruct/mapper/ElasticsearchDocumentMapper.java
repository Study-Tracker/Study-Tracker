package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchCollaboratorDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchCommentDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchConclusionsDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchFolderDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchKeywordDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchLinkDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchProgramDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchUserDocument;
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

  /** For mapping the {@link ExternalLink#getUrl()} field to a String **/
  default String map(URL url) {
    return url.toString();
  }

}
