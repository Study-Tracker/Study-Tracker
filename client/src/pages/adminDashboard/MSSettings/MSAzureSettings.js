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
import NotyfContext from "../../../context/NotyfContext";
import axios from "axios";
import {Button, Card, Col, Row} from "react-bootstrap";
import GraphSettingsDetailsCard from "./GraphSettingsDetailsCard";
import MSGraphIntegrationFormModal from "./MSGraphIntegrationFormModal";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-regular-svg-icons";
import SharePointSiteCard from "./SharePointSiteCard";
import SharePointSiteFormModal from "./SharePointSiteFormModal";
import OneDriveDriveCard from "./OneDriveDriveCard";
import {faPlus} from "@fortawesome/free-solid-svg-icons";

const MSAzureSettings = () => {

  const [settings, setSettings] = useState(null);
  const [drives, setDrives] = useState([]);
  const [sites, setSites] = useState([]);
  const [loadCount, setLoadCount] = useState(0);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [sharePointSiteModalIsOpen, setSharePointSiteModalIsOpen] = useState(false);
  const [oneDriveDriveModalIsOpen, setOneDriveDriveModalIsOpen] = useState(false);
  const notyf = useContext(NotyfContext);
  const integrationFormikRef = useRef();
  const siteFormikRef = useRef();

  useEffect(() => {

    axios.get("/api/internal/integrations/msgraph")
    .then(response => {
      if (response.data.length > 0) {
        const integration = response.data[0];
        setSettings(integration);
        axios.get("/api/internal/integrations/msgraph/" + integration.id + "/sharepoint/sites")
        .then(response => {
          setSites(response.data);
        })
        axios.get("/api/internal/integrations/msgraph/" + integration.id + "/onedrive/drives")
        .then(response => {
          setDrives(response.data);
        })
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to load Microsoft Aure settings"
      });
    })
  }, [loadCount]);

  const handleIntegrationFormSubmit = (values, {setSubmitting, resetForm}) => {
    const url = "/api/internal/integrations/msgraph/" + (values.id || '');
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
        message: "Microsoft Azure integration settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save Microsoft Azure integration settings: " + error.response.data.message
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleSharePointSiteFormSubmit = (values, {setSubmitting, resetForm}) => {
    const url = "/api/internal/integrations/msgraph/" + settings.id + "/sharepoint/sites/" + (values.id || '');
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(response => {
      setLoadCount(loadCount + 1);
      setSharePointSiteModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "SharePoint site saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save SharePoint site"
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  return (
      <>

        <Row className={"mb-3"}>
            {
              settings && settings.active ? (
                    <Col className={"d-flex justify-content-between"}>
                      <div>
                        <h3>
                          Microsoft Azure integration is
                          &nbsp;
                          <span className={"text-success"}>ENABLED</span>
                        </h3>
                      </div>
                      <div>
                        <Button
                          variant={"outline-warning"}
                          onClick={() => {
                            integrationFormikRef.current.setValues(settings);
                            setIntegrationModalIsOpen(true);
                          }}
                        >
                          <FontAwesomeIcon icon={faEdit} className={"me-2"} />
                          Edit Registration
                        </Button>
                      </div>
                    </Col>
              ) : (
                  <Col className={"d-flex justify-content-between"}>
                    <h3>
                      Microsoft Azure integration is
                      &nbsp;
                      <span className={"text-muted"}>DISABLED</span>
                    </h3>
                  </Col>
              )
            }
        </Row>

        <Row>
          <Col>
            {
              settings && settings.active ? (
                  <GraphSettingsDetailsCard settings={settings} />
              ) : (
                  <Card className="illustration">
                    <Card.Body>
                      <Row>
                        <Col>
                          <p>
                            Connect Study Tracker with Microsoft 365 services to enable file management in SharePoint and OneDrive.
                            Study Tracker uses Microsoft Graph API to access your files. To start, register a new API application
                            in Azure Active Directory, generate and an API key, and then provide the client details below.
                          </p>
                        </Col>
                      </Row>
                      <Row>
                        <Col className={"text-center"}>
                          <Button
                              size="lg"
                              color={"primary"}
                              onClick={() => setIntegrationModalIsOpen(true)}
                          >
                            Register Microsoft Azure Integration
                          </Button>
                        </Col>
                      </Row>
                    </Card.Body>
                  </Card>
              )
            }
          </Col>
        </Row>

        {/* SharePoint Sites */}
        {
          settings && settings.active && (

              <>

                <Row className={"mb-3"}>
                  <Col className={"d-flex justify-content-between"}>
                    <div>
                      <h4>SharePoint Sites</h4>
                    </div>
                    <div>
                      <Button
                          variant={"primary"}
                          onClick={() => setSharePointSiteModalIsOpen(true)}
                      >
                        <FontAwesomeIcon icon={faPlus} className={"me-2"} />
                        Add SharePoint Site
                      </Button>
                    </div>
                  </Col>
                </Row>

                {
                  sites && sites.length > 0 ? sites.map(site => {
                    return (
                      <Row>
                        <Col>
                          <SharePointSiteCard site={site} />
                        </Col>
                      </Row>
                    );
                  }) : (
                      <Card className="illustration mt-2">
                        <Card.Body>
                          <Row>
                            <Col className={"text-center p-3"}>
                              <h5>No sites have been registered.</h5>
                              <p>
                                Add SharePoint sites to connect them and their OneDrive folders to Study Tracker.
                                You can add as many sites as you like.
                              </p>
                            </Col>
                          </Row>
                        </Card.Body>
                      </Card>
                  )
                }

              </>
          )
        }

        {/* OneDrive */}
        {
          settings && settings.active && (
                <>

                  <Row className={"mb-3"}>
                    <Col className={"d-flex justify-content-between"}>
                      <div>
                        <h4>OneDrive Drives</h4>
                      </div>
                      <div>
                        <Button
                            variant={"primary"}
                            onClick={() => setOneDriveDriveModalIsOpen(true)}
                        >
                          <FontAwesomeIcon icon={faPlus} className={"me-2"} />
                          Add OneDrive Drive
                        </Button>
                      </div>
                    </Col>
                  </Row>

                  {
                    drives && drives.length > 0 ? drives.map(drive => {
                      return (
                          <Row>
                            <Col>
                              <OneDriveDriveCard drive={drive} />
                            </Col>
                          </Row>
                      );
                    }) : (
                        <Card className="illustration mt-2">
                          <Card.Body>
                            <Row>
                              <Col className={"text-center p-3"}>
                                <h5>No drives have been registered.</h5>
                                <p>
                                  Add OneDrive drives by registering SharePoint sites. The drives will be added as
                                  storage locations, which can be used for study or data file management.
                                </p>
                              </Col>
                            </Row>
                          </Card.Body>
                        </Card>
                    )
                  }

                </>
          )
        }

        <MSGraphIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={integrationFormikRef}
        />

        <SharePointSiteFormModal
            isOpen={sharePointSiteModalIsOpen}
            setIsOpen={setSharePointSiteModalIsOpen}
            handleFormSubmit={handleSharePointSiteFormSubmit}
            formikRef={siteFormikRef}
            integration={settings}
        />

      </>
  )

}

export default MSAzureSettings;