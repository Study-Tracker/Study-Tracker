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

import React, {useContext, useEffect, useRef, useState} from "react";
import axios from "axios";
import {Button, Card, Col, Row} from "react-bootstrap";
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
  const [selectedLocation, setSelectedLocation] = useState(null);
  const formikRef = useRef();

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

  const handleSubmitForm = (values, {setSubmitting, resetForm}) => {
    const url = selectedLocation
        ? "/api/internal/storage-locations/" + selectedLocation.id
        : "/api/internal/storage-locations";
    const method = selectedLocation ? "PUT" : "POST";
    axios({
      method: method,
      url: url,
      data: values,
      headers: {
        "Content-Type": "application/json"
      },
    })
    .then(response => {
      notyf.open({message: 'Storage location saved.', type: 'success'});
      resetForm();
      setShowModal(false);
    })
    .catch(e => {
      console.error(e);
      if (e.response.status === 404) {
        notyf.open({message: 'The requested folder does not exist: ' + values.rootFolderPath, type: 'error'});
      } else {
        notyf.open({message: 'Failed to save storage location.', type: 'error'});
      }
    })
    .finally(() => {
      setSubmitting(false);
      setLoadCounter(loadCounter + 1);
    })
  }

  const handleLocationEdit = (location) => {
    console.debug("Edit location: ", location);
    formikRef.current?.resetForm();
    setSelectedLocation(location);
    setShowModal(true);
  }

  const handleLocationActiveToggle = (location, active) => {

  }

  let content = '';
  if (isLoading) content = <SettingsLoadingMessage />;
  else if (error) content = <SettingsErrorMessage />;
  else content = <StorageLocationTable
          locations={locations}
          handleLocationEdit={handleLocationEdit}
          handleToggleLocationActive={handleLocationActiveToggle}
      />;

  return (
      <Card>

        <Card.Header>
          <Card.Title tag={"h5"}>
            Storage Locations
            <span className="float-end">
              <Button
                  variant={"primary"}
                  onClick={() => {
                    formikRef.current?.resetForm();
                    setSelectedLocation(null);
                    setShowModal(true);
                  }}
              >
                Add Location
                &nbsp;
                <FolderPlus className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>

        <Card.Body>

          <Row>
            <Col>
              <div className="info-alert">
                Storage locations are folders in local file systems or cloud services that Study Tracker
                users can use to store their data. A default storage location will be created on
                application startup, dependent on the <code>storage.mode</code> setting used, which
                will determine where program, study, and assay folders are created by default. You
                can add additional storage locations here from the available configured storage systems.
              </div>
            </Col>
          </Row>

          <Row>
            <Col>
              {content}
            </Col>
          </Row>

          <StorageLocationFormModal
              isOpen={showModal} 
              setIsOpen={setShowModal}
              selectedLocation={selectedLocation}
              handleFormSubmit={handleSubmitForm}
              formikRef={formikRef}
          />
        </Card.Body>

      </Card>

  )

}

export default StorageLocationSettings;