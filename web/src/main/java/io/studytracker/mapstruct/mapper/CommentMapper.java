package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.CommentDto;
import io.studytracker.mapstruct.dto.api.CommentPayloadDto;
import io.studytracker.mapstruct.dto.form.CommentFormDto;
import io.studytracker.mapstruct.dto.response.CommentDetailsDto;
import io.studytracker.model.Comment;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  Comment fromDetailsDto(CommentDetailsDto dto);

  Comment fromFormDto(CommentFormDto dto);

  List<Comment> fromDetailsDtoList(List<CommentDetailsDto> dtos);

  Set<Comment> fromDetailsDtoSet(Set<CommentDetailsDto> dtos);

  CommentDetailsDto toDetailsDto(Comment comment);

  List<CommentDetailsDto> toDetailsDtoList(List<Comment> comments);

  Set<CommentDetailsDto> toDetailsDtoSet(Set<Comment> comments);

  // API

  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "studyId", source = "study.id")
  CommentDto toDto(Comment comment);
  List<CommentDto> toDtoList(List<Comment> comments);
  Comment fromPayloadDto(CommentPayloadDto dto);
}
