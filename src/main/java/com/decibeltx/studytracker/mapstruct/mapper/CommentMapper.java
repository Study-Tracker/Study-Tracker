package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.CommentDto;
import com.decibeltx.studytracker.model.Comment;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  Comment fromDto(CommentDto dto);
  List<Comment> fromDtoList(List<CommentDto> dtos);
  Set<Comment> fromDtoSet(Set<CommentDto> dtos);
  CommentDto toDto(Comment comment);
  List<CommentDto> toDtoList(List<Comment> comments);
  Set<CommentDto> toDtoSet(Set<Comment> comments);
}
