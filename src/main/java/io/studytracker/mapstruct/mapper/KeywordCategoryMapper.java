package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.KeywordCategoryFormDto;
import io.studytracker.mapstruct.dto.response.KeywordCategoryDto;
import io.studytracker.model.KeywordCategory;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeywordCategoryMapper {

  KeywordCategory fromKeywordCategoryDto(KeywordCategoryDto dto);
  KeywordCategory fromKeywordCategoryFormDto(KeywordCategoryFormDto dto);
  Set<KeywordCategory> fromKeywordCategoryDtoSet(Set<KeywordCategoryDto> dtos);
  List<KeywordCategory> fromKeywordCategoryDtoList(List<KeywordCategoryDto> dtos);

  KeywordCategoryDto toKeywordCategoryDto(KeywordCategory category);
  Set<KeywordCategoryDto> toKeywordCategoryDtoSet(Set<KeywordCategory> categories);
  List<KeywordCategoryDto> toKeywordCategoryDtoList(List<KeywordCategory> categories);

}
