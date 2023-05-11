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
import {Button, Card,} from 'react-bootstrap';
import {FolderPlus} from 'react-feather';
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import axios from "axios";
import ProgramsTable from "./ProgramsTable";
import ProgramDetailsModal from "./ProgramDetailsModal";

const ProgramSettings = () => {
  
  const [state, setState] = useState({
    programs: [],
    isLoaded: false,
    isError: false,
    showDetails: false,
    selectedProgram: null,
    isModalOpen: false
  });

  const showModal = (selected) => {
    console.debug(selected);
    if (!!selected) {
      setState(prevState => ({
        ...prevState,
        selectedProgram: selected,
        isModalOpen: true
      }));
    } else {
      setState(prevState => ({
        ...prevState,
        isModalOpen: false,
        selectedProgram: null
      }))
    }
  };

  useEffect(() => {
    axios.get("/api/internal/program?details=true")
    .then(async response => {
      setState(prevState => ({
        ...prevState,
        programs: response.data,
        isLoaded: true
      }))
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });
  }, []);

  let content = '';
  if (state.isLoaded) {
    content = (
        <ProgramsTable
            programs={state.programs}
            showModal={showModal}
        />
    );
  } else if (state.isError) {
    content = <SettingsErrorMessage/>;
  } else {
    content = <SettingsLoadingMessage/>;
  }

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0">
            Registered Programs
            <span className="float-end">
              <Button
                  variant={"primary"}
                  href={"/programs/new"}
              >
                New Program
                &nbsp;
                <FolderPlus className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>

        <Card.Body>

          {content}

          <ProgramDetailsModal
              isOpen={state.isModalOpen}
              program={state.selectedProgram}
              showModal={showModal}
          />

        </Card.Body>

      </Card>
  );

}

export default ProgramSettings;