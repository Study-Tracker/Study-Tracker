package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.AssayDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.AssayFormDto;
import com.decibeltx.studytracker.mapstruct.dto.AssayParentDto;
import com.decibeltx.studytracker.mapstruct.dto.AssaySlimDto;
import com.decibeltx.studytracker.mapstruct.dto.AssaySummaryDto;
import com.decibeltx.studytracker.model.Assay;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayMapper {

  Assay fromAssayForm(AssayFormDto dto);

  Assay fromAssayDetails(AssayDetailsDto dto);
  Set<Assay> fromAssayDetailsSet(Set<AssayDetailsDto> dtos);
  List<Assay> fromAssayDetailsList(Set<AssayDetailsDto> dtos);
  AssayDetailsDto toAssayDetails(Assay assay);
  Set<AssayDetailsDto> toAssayDetailsSet(Set<Assay> assays);
  List<AssayDetailsDto> toAssayDetailsList(List<Assay> assays);

  Assay fromAssaySummary(AssaySummaryDto dto);
  Set<Assay> fromAssaySummarySet(Set<AssaySummaryDto> dtos);
  List<Assay> fromAssaySummaryList(Set<AssaySummaryDto> dtos);
  AssaySummaryDto toAssaySummary(Assay assay);
  Set<AssaySummaryDto> toAssaySummarySet(Set<Assay> assays);
  List<AssaySummaryDto> toAssaySummaryList(List<Assay> assays);

  Assay fromAssaySlim(AssaySlimDto dto);
  Set<Assay> fromAssaySlimSet(Set<AssaySlimDto> dtos);
  List<Assay> fromAssaySlimList(Set<AssaySlimDto> dtos);
  AssaySlimDto toAssaySlim(Assay assay);
  Set<AssaySlimDto> toAssaySlimSet(Set<Assay> assays);
  List<AssaySlimDto> toAssaySlimList(List<Assay> assays);

  Assay fromAssayParent(AssayParentDto dto);
  Set<Assay> fromAssayParentSet(Set<AssayParentDto> dtos);
  List<Assay> fromAssayParentList(Set<AssayParentDto> dtos);
  AssayParentDto toAssayParent(Assay assay);
  Set<AssayParentDto> toAssayParentSet(Set<Assay> assays);
  List<AssayParentDto> toAssayParentList(List<Assay> assays);

}
