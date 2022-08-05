package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.KeywordCategoryDto;
import io.studytracker.mapstruct.dto.api.KeywordCategoryPayloadDto;
import io.studytracker.mapstruct.dto.form.KeywordCategoryFormDto;
import io.studytracker.mapstruct.dto.response.KeywordCategoryDetailsDto;
import io.studytracker.model.KeywordCategory;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeywordCategoryMapper {

  KeywordCategory fromKeywordCategoryDto(KeywordCategoryDetailsDto dto);
  KeywordCategory fromKeywordCategoryFormDto(KeywordCategoryFormDto dto);
  Set<KeywordCategory> fromKeywordCategoryDtoSet(Set<KeywordCategoryDetailsDto> dtos);
  List<KeywordCategory> fromKeywordCategoryDtoList(List<KeywordCategoryDetailsDto> dtos);

  KeywordCategoryDetailsDto toKeywordCategoryDto(KeywordCategory category);
  Set<KeywordCategoryDetailsDto> toKeywordCategoryDtoSet(Set<KeywordCategory> categories);
  List<KeywordCategoryDetailsDto> toKeywordCategoryDtoList(List<KeywordCategory> categories);

  KeywordCategoryDto toDto(KeywordCategory category);
  List<KeywordCategoryDto> toDtoList(List<KeywordCategory> categories);
  KeywordCategory fromPayloadDto(KeywordCategoryPayloadDto dto);

}
