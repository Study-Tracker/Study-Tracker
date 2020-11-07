/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {FormFeedback, FormGroup, Label} from "reactstrap";
import Select from "react-select";

export const ProgramDropdown = ({programs, selectedProgram, onChange, isValid, disabled, isLegacyStudy}) => {

  const programOptions = programs
  .filter(p => !!isLegacyStudy || !!p.active)
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(program => {
    return {
      value: program.id,
      label: !!program.subProgram ? program.name + ": " + program.subProgram
          : program.name
    };
  });

  return (
      <FormGroup>
        <Label>Program *</Label>
        <Select
            className={"react-select-container "}
            invalid={!isValid}
            classNamePrefix="react-select"
            value={programOptions.filter(option => {
              return option.value === selectedProgram
            })}
            options={programOptions}
            onChange={(selected) => {
              const program = programs.filter(p => {
                return p.id === selected.value;
              })[0];
              onChange({"program": program});
            }}
            isDisabled={disabled}
        />
        <FormFeedback>Select the program your study is associated
          with.</FormFeedback>
      </FormGroup>
  );

};