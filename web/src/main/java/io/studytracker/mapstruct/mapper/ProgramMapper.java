package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.ProgramDto;
import io.studytracker.mapstruct.dto.api.ProgramPayloadDto;
import io.studytracker.mapstruct.dto.form.ProgramFormDto;
import io.studytracker.mapstruct.dto.response.ProgramDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.model.Program;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

  // ProgramSummary
  Program fromProgramSummary(ProgramSummaryDto dto);

  List<Program> fromProgramSummaryList(List<ProgramSummaryDto> dtos);

  ProgramSummaryDto toProgramSummary(Program program);

  List<ProgramSummaryDto> toProgramSummaryList(List<Program> programs);

  // ProgramDetails
  Program fromProgramDetails(ProgramDetailsDto dto);

  List<Program> fromProgramDetailsList(List<ProgramDetailsDto> dtos);

  ProgramDetailsDto toProgramDetails(Program program);

  List<ProgramDetailsDto> toProgramDetailsList(List<Program> programs);

  ProgramFormDto toProgramFormDto(Program program);
  Program fromProgramFormDto(ProgramFormDto dto);

  // API
  List<ProgramDto> toProgramDtoList(List<Program> programs);
  @Mapping(source = "createdBy.id", target = "createdBy")
  @Mapping(source = "lastModifiedBy.id", target = "lastModifiedBy")
  ProgramDto toProgramDto(Program program);

  ProgramPayloadDto toProgramPayloadDto(Program program);
  Program fromProgramPayloadDto(ProgramPayloadDto dto);
}
