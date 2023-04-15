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

import React, {useContext, useEffect, useRef, useState} from "react";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import AWSIntegrationDetailsCard from "./AWSIntegrationDetailsCard";
import AWSIntegrationSetupCard from "./AWSIntegrationSetupCard";
import S3BucketCard from "./S3BucketCard";
import {Button, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faCircleXmark,
  faEdit
} from "@fortawesome/free-regular-svg-icons";
import AWSIntegrationFormModal from "./AWSIntegrationFormModal";
import {faGears, faPlus, faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import S3BucketFormModal from "./S3BucketFormModal";

const AWSIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [bucketModalIsOpen, setBucketModalIsOpen] = useState(false);
  const [drives, setDrives] = useState([]);
  const [loadCount, setLoadCount] = useState(0);
  const notyf = useContext(NotyfContext);
  const integrationFormikRef = useRef();
  const bucketFormikRef = useRef();

  useEffect(() => {
    axios.get("/api/internal/integrations/aws")
    .then(response => {

      console.debug("AWS settings loaded", response.data);

      if (response.data.length === 0) {
        console.warn("No AWS settings found");
        setSettings(null);
      } else if (response.data.length === 1) {
        setSettings(response.data[0]);
      } else {
        console.warn("Multiple AWS settings found", response.data);
        setSettings(response.data[0]);
      }

      axios.get("/api/internal/drives/s3/")
      .then(response => {
        console.debug("S3 buckets loaded", response.data);
        setDrives(response.data);
      })

    })
    .catch(error => {
      console.error("Failed to load AWS settings", error);
      notyf.open({
        type: "error",
        message: "Failed to load AWS settings"
      });
    });
  }, [loadCount]);

  const handleIntegrationFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("Saving AWS integration settings", values);
    const url = "/api/internal/integrations/aws/" + (values.id || '');
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(response => {
      setLoadCount(loadCount + 1);
      setIntegrationModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "AWS integration settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save AWS integration settings: " + error.response.data.message
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleBucketFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("Saving S3 bucket settings", values);
    const url = "/api/internal/drives/s3/" + (values.id || '');
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(response => {
      setLoadCount(loadCount + 1);
      setBucketModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "S3 bucket settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save S3 bucket settings: " + error.response.data.message
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleStatusToggle = (active) => {
    axios.patch("/api/internal/integrations/aws/" + settings.id + "?active=" + active)
    .then(response => {
      setLoadCount(loadCount + 1);
      notyf.open({
        type: "success",
        message: "AWS integration settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save AWS integration settings: " + error.response.data.message
      });
    });
  }

  const handleBucketStatusUpdate = (id, active) => {
    axios.patch("/api/internal/drives/s3/" + id + "?active=" + active)
    .then(response => {
      setLoadCount(loadCount + 1);
      notyf.open({
        type: "success",
        message: "S3 Bucket status updated"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to update S3 bucket status: " + error.response.data.message
      });
    });
  }

  return (
      <>

        <Row className={"mb-3"}>
          <Col className={"d-flex justify-content-between"}>

            <div>
              {
                  settings && settings.active ? (
                      <h3>
                        Amazon Web Services integration is
                        &nbsp;
                        <span className={"text-success"}>ENABLED</span>
                      </h3>
                  ) : (
                      <h3>
                        Amazon Web Services integration is
                        &nbsp;
                        <span className={"text-muted"}>DISABLED</span>
                      </h3>
                  )
              }
            </div>

            <div>
              <Dropdown>

                <Dropdown.Toggle variant={"primary"} id="dropdown-basic">
                  <FontAwesomeIcon icon={faGears} className={"me-2"} />
                  Settings
                  &nbsp;&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  {
                    settings ? (
                        <Dropdown.Item onClick={() => {
                          integrationFormikRef.current.setValues(settings);
                          setIntegrationModalIsOpen(true);
                        }}>
                          <FontAwesomeIcon icon={faEdit} className={"me-2"} />
                          Edit Registration
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item onClick={() => {
                          setIntegrationModalIsOpen(true);
                        }}>
                          <FontAwesomeIcon icon={faPlusCircle} className={"me-2"} />
                          Add Registration
                        </Dropdown.Item>
                    )
                  }

                  {
                    settings && settings.active ? (
                        <Dropdown.Item onClick={() => handleStatusToggle(false)}>
                          <FontAwesomeIcon icon={faCircleXmark} className={"me-2"} />
                          Disable Integration
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item onClick={() => handleStatusToggle(true)}>
                          <FontAwesomeIcon icon={faCheckCircle} className={"me-2"} />
                          Re-enable Integration
                        </Dropdown.Item>
                    )
                  }

                </Dropdown.Menu>

              </Dropdown>
            </div>

          </Col>
        </Row>

        {
          settings
              ? <AWSIntegrationDetailsCard settings={settings} />
              : <AWSIntegrationSetupCard handleClick={() => setIntegrationModalIsOpen(true)} />
        }

        {
          settings && (
                <Row className={"mb-3"}>
                  <Col className={"d-flex justify-content-between"}>

                    <div>
                      <h4>S3 Buckets</h4>
                    </div>

                    <div>
                      <Button
                          variant={"primary"}
                          onClick={() => setBucketModalIsOpen(true)}
                          disabled={!settings.active}
                      >
                        <FontAwesomeIcon icon={faPlus} className={"me-2"} />
                        Add S3 Bucket
                      </Button>
                    </div>

                  </Col>
                </Row>
            )
        }

        {
          drives.length > 0 && (
              drives
              .sort((a, b) => a.name.localeCompare(b.storageDrive.displayName))
              .map(drive => (
                  <S3BucketCard
                      bucket={drive}
                      handleStatusUpdate={handleBucketStatusUpdate}
                  />
              ))
          )
        }

        <AWSIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={integrationFormikRef}
        />

        <S3BucketFormModal
            isOpen={bucketModalIsOpen}
            setIsOpen={setBucketModalIsOpen}
            handleFormSubmit={handleBucketFormSubmit}
            formikRef={bucketFormikRef}
        />

      </>
  )
}

export default AWSIntegrationSettings;