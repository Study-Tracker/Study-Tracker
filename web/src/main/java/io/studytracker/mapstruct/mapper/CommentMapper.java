package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.CommentFormDto;
import io.studytracker.mapstruct.dto.response.CommentDto;
import io.studytracker.model.Comment;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  Comment fromDto(CommentDto dto);

  Comment fromFormDto(CommentFormDto dto);

  List<Comment> fromDtoList(List<CommentDto> dtos);

  Set<Comment> fromDtoSet(Set<CommentDto> dtos);

  CommentDto toDto(Comment comment);

  List<CommentDto> toDtoList(List<Comment> comments);

  Set<CommentDto> toDtoSet(Set<Comment> comments);
}
