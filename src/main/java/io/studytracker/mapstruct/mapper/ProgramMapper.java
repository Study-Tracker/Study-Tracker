package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.ProgramDetailsDto;
import io.studytracker.mapstruct.dto.ProgramSummaryDto;
import io.studytracker.model.Program;
import java.util.List;
import org.mapstruct.Mapper;

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
}
