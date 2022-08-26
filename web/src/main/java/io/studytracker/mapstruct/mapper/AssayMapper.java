package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.AssayDto;
import io.studytracker.mapstruct.dto.api.AssayPayloadDto;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.response.AssayDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayParentDto;
import io.studytracker.mapstruct.dto.response.AssaySlimDto;
import io.studytracker.mapstruct.dto.response.AssaySummaryDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.User;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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

  @Mapping(target = "owner", source = "owner.id")
  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studyId", source = "study.id")
  @Mapping(target = "assayTypeId", source = "assayType.id")
  @Mapping(target = "notebookFolderId", source = "notebookFolder.id")
  @Mapping(target = "storageFolderId", source = "storageFolder.id")
  @Mapping(target = "users", source = "users", qualifiedByName = "userToId")
  @Mapping(target = "tasks", source = "tasks", qualifiedByName = "assayTaskToId")
  AssayDto toAssayDto(Assay assay);

  List<AssayDto> toAssayDtoList(List<Assay> assays);

  @Mapping(target = "owner", ignore = true)
  @Mapping(target = "users", ignore = true)
  Assay fromPayload(AssayPayloadDto dto);

  @Named("userToId")
  public static Long userToId(User user) { return user.getId();}

  @Named("assayTaskToId")
  public static Long assayTaskToId(AssayTask assayTask) { return assayTask.getId();}

}
