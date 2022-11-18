/*
 * Copyright 2022 the original author or authors.
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
import axios from "axios";
import {Button, Card} from "react-bootstrap";
import {FolderPlus} from "react-feather";
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import StorageLocationTable from "./StorageLocationTable";
import StorageLocationFormModal
  from "../../../common/modals/StorageLocationFormModal";
import NotyfContext from "../../../context/NotyfContext";

const StorageLocationSettings = () => {

  const notyf = useContext(NotyfContext);

  const [locations, setLocations] = useState([]);
  const [loadCounter, setLoadCounter] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);
    axios.get("/api/internal/storage-locations")
    .then(response => {
      setLocations(response.data);
    })
    .catch(e => {
      console.error(e);
      setError(e)
      notyf.open({message: 'Failed to load available storage locations.', type: 'error'});
    })
    .finally(() => {
      setIsLoading(false);
    });
  }, [loadCounter]);

  let content = '';
  if (isLoading) content = <SettingsLoadingMessage />;
  else if (error) content = <SettingsErrorMessage />;
  else content = <StorageLocationTable locations={locations} />;

  return (
      <Card>

        <Card.Header>
          <Card.Title tag={"h5"}>
            Storage Locations
            <span className="float-end">
              <Button
                  variant={"primary"}
                  onClick={() => setShowModal(true)}
              >
                Add Location
                &nbsp;
                <FolderPlus className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>

        <Card.Body>
          {content}
          <StorageLocationFormModal
              isOpen={showModal} 
              setIsOpen={setShowModal}
              locations={locations}
          />
        </Card.Body>

      </Card>

  )

}

export default StorageLocationSettings;