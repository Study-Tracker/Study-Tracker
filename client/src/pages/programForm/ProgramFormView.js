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

import React, {useContext, useEffect, useState} from "react";
import NoSidebarPageWrapper from "../../common/structure/NoSidebarPageWrapper";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import ProgramForm from "./ProgramForm";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";

const ProgramFormView = () => {

  const params = useParams();
  const programId = params.programId || null;
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);
  const [selectedProgram, setSelectedProgram] = useState(null);
  const [programs, setPrograms] = useState(null);
  const [error, setError] = useState(null);
  const [elnProjects, setElnProjects] = useState(null);
  const [rootFolders, setRootFolders] = useState([]);
  const [gitGroups, setGitGroups] = useState(null);
  const notyf = useContext(NotyfContext);
  
  useEffect(() => {

    // Programs
    axios.get("/api/internal/program")
    .then(async response => {

      const programs = response.data;
      console.debug("Existing programs", programs);
      setPrograms(programs);

      // Get the full record for the requested program
      if (programId) {
        const program = await axios.get("/api/internal/program/" + programId)
          .then(response => response.data);
        console.debug("Selected Program", program);
        setSelectedProgram(program);
      }

      // Get Git project groups
      axios.get("/api/internal/git-groups?root=true")
      .then(response => setGitGroups(response.data))
      .catch(error => {
        console.error("Error loading Git groups", error);
        setError(error);
        notyf.open({
          type: "error",
          message: "Error loading Git project groups"
        });
      })

      // Load ELN projects
      if (features && features.notebook && features.notebook.isEnabled) {
        const projects = await axios.get("/api/internal/eln/project-folders")
          .then(response => {
            return response.data.map(p => {
              return {
                name: p.name,
                url: p.url,
                folderId: p.referenceId
              }
            })
          })
          .catch(error => {
            console.error("Error getting ELN programs", error);
            setError(error);
          });
        setElnProjects(projects);
      }

    }).catch(error => {
      console.error("Error loading programs", error);
      setError(error);
    });

    axios.get("/api/internal/storage-drive-folders?studyRoot=true")
    .then(response => {
      setRootFolders(response.data);
    })
    .catch(error => {
      console.error("Error loading root folders", error);
      setError(error);
    });

  }, [programId, features, notyf]);

  let content = <LoadingMessage/>;
  if (error) {
    content = <ErrorMessage/>;
  } else if (!!user && programs && (!programId || selectedProgram)) {
    content = (
        <ProgramForm
            program={selectedProgram}
            programs={programs}
            user={user}
            features={features}
            elnProjects={elnProjects}
            rootFolders={rootFolders}
            gitGroups={gitGroups}
        />
    );
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

export default ProgramFormView;
