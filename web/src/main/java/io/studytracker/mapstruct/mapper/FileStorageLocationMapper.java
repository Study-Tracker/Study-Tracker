package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.FileStorageLocationFormDto;
import io.studytracker.mapstruct.dto.response.FileStorageLocationDetailsDto;
import io.studytracker.model.FileStorageLocation;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileStorageLocationMapper {

  FileStorageLocation fromForm(FileStorageLocationFormDto dto);

  FileStorageLocationDetailsDto toDetails(FileStorageLocation fileStorageLocation);
  Set<FileStorageLocationDetailsDto> toDetailsSet(Set<FileStorageLocation> fileStorageLocations);
  List<FileStorageLocationDetailsDto> toDetailsList(List<FileStorageLocation> fileStorageLocations);

}
