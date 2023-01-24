/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useEffect, useState} from "react";
import {FormGroup} from "./common";
import {Form} from "react-bootstrap";
import Select from "react-select";

export const ProgramDropdown = ({
  programs,
  selectedProgram,
  onChange,
  isInvalid,
  disabled,
  isLegacyStudy
}) => {

  const [programOptions, setProgramOptions] = useState([]);

  useEffect(() => {
    const options = programs
    .filter(p => isLegacyStudy || p.active)
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
    setProgramOptions(options);
  }, [programs, isLegacyStudy]);

  return (
      <FormGroup>
        <Form.Label>Program *</Form.Label>
        <Select
            className={"react-select-container " + (isInvalid ? "is-invalid" : "")}
            invalid={isInvalid}
            classNamePrefix="react-select"
            value={programOptions.filter(option => {
              return option.value === selectedProgram
            })}
            options={programOptions}
            onChange={(selected) => {
              const program = programs.filter(p => {
                return p.id === selected.value;
              })[0];
              onChange(program);
            }}
            isDisabled={disabled}
        />
        <Form.Control.Feedback type={"invalid"}>
          You must select a program.
        </Form.Control.Feedback>
        <Form.Text>
          Select the program your study is associated with.
        </Form.Text>
      </FormGroup>
  );

};