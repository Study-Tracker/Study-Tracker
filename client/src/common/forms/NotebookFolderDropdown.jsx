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

import React, {useContext} from "react";
import {Form, Spinner} from "react-bootstrap";
import {FormGroup} from "./common";
import PropTypes from "prop-types";
import axios from "axios";
import Select from "react-select";
import {useQuery} from "@tanstack/react-query";
import NotyfContext from "../../context/NotyfContext";

const NotebookFolderDropdown = ({onChange, parentFolder}) => {

  const notyf = useContext(NotyfContext);

  const {data: folders, isLoading} = useQuery({
    queryKey: ["notebookFolder", parentFolder?.referenceId],
    queryFn: () => {
      return axios.get(
        `/api/internal/eln/folder/${parentFolder.referenceId}?loadContents=true`)
      .then(response => response.data.subFolders || [])
      .catch(error => {
        notyf.error("Error loading notebook folders: " + error.message);
        console.error("Error loading notebook folders: ", error);
        return error;
      })
    },
    enabled: !!parentFolder,
    initialData: []
  });

  const options = folders.sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    }
    if (a.name < b.name) {
      return -1;
    }
    return 0;
  })
  .map(t => {
    return {
      value: t,
      label: t.name
    };
  });

  console.debug("Notebook Subfolders: ", folders);
  console.debug("NotebookFolderDropdown options: ", options);

  return (
      <FormGroup>
        <Form.Label>Notebook Folder Path</Form.Label>
        <Select
          placeholder={"Search for an select a folder..."}
          className={"react-select-container"}
          classNamePrefix="react-select"
          options={options}
          onChange={(selected) => {
            console.debug("Selected folder: ", selected.value);
            onChange(selected.value);
          }}
          defaultOptions={true}
          menuPortalTarget={document.body}
          isClearable={true}
        />
        <div hidden={!isLoading}>
          <Spinner animation={"border"} role={"status"} variant={"primary"} size={"sm"} />&nbsp;Loading folders...
        </div>
        <Form.Text hidden={!!parentFolder} className={"text-danger"}>
          You must select a program to associate the study with.<br />
        </Form.Text>
        <Form.Text>
          Select a notebook folder to use for your study. Only folders in the selected Program&apos;s project folder will
          be selectable.
        </Form.Text>
      </FormGroup>
  );
}

NotebookFolderDropdown.propTypes = {
  onChange: PropTypes.func.isRequired,
  parentFolder: PropTypes.object,
}

export default NotebookFolderDropdown;