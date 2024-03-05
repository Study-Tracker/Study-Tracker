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

import React, {useContext, useState} from "react";
import {Button, Card,} from 'react-bootstrap';
import {FolderPlus} from 'react-feather';
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import axios from "axios";
import ProgramsTable from "./ProgramsTable";
import ProgramDetailsModal from "./ProgramDetailsModal";
import NotyfContext from "../../../context/NotyfContext";
import {useQuery} from "react-query";

const ProgramSettings = () => {

  const [loadCount, setLoadCount] = useState(0);
  // const [showDetails, setShowDetails] = useState(false);
  const [selectedProgram, setSelectedProgram] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const notyf = useContext(NotyfContext);

  const showModal = (selected) => {
    console.debug(selected);
    if (!!selected) {
      setSelectedProgram(selected);
      setModalIsOpen(true);
    } else {
      setModalIsOpen(false);
      setSelectedProgram(null);
    }
  };

  const {data: programs, isLoading, error} = useQuery('programs', () => {
    return axios.get("/api/internal/program?details=true")
    .then(response => response.data)
    .catch(error => {
      console.error(error);
      notyf.error("Failed to load programs");
      return error;
    });
  });

  const handleStatusChange = (id, active) => {
    axios.post("/api/internal/program/" + id + "/status?active=" + active)
    .then(() => {
      setLoadCount(loadCount + 1);
      notyf.open({
        type: "success",
        message: "Program status updated"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to update program status"
      });
    })
  }

  let content = <SettingsLoadingMessage/>;
  if (error) {
    content = <SettingsErrorMessage/>;
  } else if (!isLoading && !error && programs) {
    content = (
        <ProgramsTable
            programs={programs}
            showModal={showModal}
            handleStatusChange={handleStatusChange}
        />
    );
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
              isOpen={modalIsOpen}
              program={selectedProgram}
              showModal={showModal}
          />

        </Card.Body>

      </Card>
  );

}

export default ProgramSettings;