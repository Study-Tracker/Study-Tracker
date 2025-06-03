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

import React, {useContext, useRef, useState} from "react";
import NotyfContext from "../../../context/NotyfContext";
import axios from "axios";
import { Button, Card, Col, Dropdown, Row } from "react-bootstrap";
import GraphSettingsDetailsCard from "./GraphSettingsDetailsCard";
import MSGraphIntegrationFormModal from "./MSGraphIntegrationFormModal";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-regular-svg-icons";
import SharePointSiteCard from "./SharePointSiteCard";
import SharePointSiteFormModal from "./SharePointSiteFormModal";
import OneDriveDriveCard from "./OneDriveDriveCard";
import { faBolt, faPlus, faTrash } from "@fortawesome/free-solid-svg-icons";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { LoadingMessageCard } from "@/common/loading";
import swal from "sweetalert2";

const MSAzureSettings = () => {

  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [sharePointSiteModalIsOpen, setSharePointSiteModalIsOpen] = useState(false);
  const notyf = useContext(NotyfContext);
  const integrationFormikRef = useRef();
  const siteFormikRef = useRef();
  const queryClient = useQueryClient();

  const {
    data: settings,
    isLoading: settingsLoading,
    error: settingsError
  } = useQuery({
    queryKey: ['msGraphSettings'],
    queryFn: () => axios.get("/api/internal/integrations/msgraph")
      .then(response => {
        if (response.data.length > 0) return response.data[0];
        else return null;
      })
      .catch(e => {
        console.error("Error loading MS Graph settings:", e);
        notyf.error("Failed to load Microsoft Azure settings");
      })
  });

  const {
    data: sites,
    isLoading: sitesLoading,
    error: sitesError
  } = useQuery({
    queryKey: ['sharepointSites', settings?.id],
    queryFn: () => axios.get(`/api/internal/integrations/msgraph/${settings?.id}/sharepoint/sites`)
      .then(response => response.data)
      .catch(e => {
        console.error("Error loading SharePoint sites:", e);
        notyf.error("Failed to load SharePoint sites");
      }),
    enabled: !!settings && settings.active,
    placeholderData: [],
  });

  const {
    data: drives,
    isLoading: drivesIsLoading,
    error: drivesError
  } = useQuery({
    queryKey: ['oneDriveDrives', settings?.id],
    queryFn: () => axios.get(`/api/internal/integrations/msgraph/${settings?.id}/onedrive/drives`)
    .then(response => response.data)
    .catch(e => {
      console.error("Error loading OneDrive drives:", e);
      notyf.error("Failed to load OneDrive drives");
    }),
    enabled: !!settings && settings.active,
    placeholderData: [],
  });

  const handleIntegrationFormSubmit = (values, {setSubmitting, resetForm}) => {
    const url = `/api/internal/integrations/msgraph${values.id ? '/' + values.id : ''}`;
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(() => {
      setIntegrationModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "Microsoft Azure integration settings saved"
      });
      queryClient.invalidateQueries({ queryKey: ['msGraphSettings'] });
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
    const url = `/api/internal/integrations/msgraph/${settings.id}/sharepoint/sites${values.id ? '/' + values.id : ''}`;
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(() => {
      setSharePointSiteModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "SharePoint site saved"
      });
      queryClient.invalidateQueries({ queryKey: ['sharepointSites', settings.id] });
      queryClient.invalidateQueries({ queryKey: ['oneDriveDrives', settings.id] });
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

  const handleRemoveUnusedDrives = () => {
    swal.fire({
      title: "Remove Unused Drives?",
      text: "This will remove all SharePoint drives that are not currently in "
        + "use with any programs or shared folders. Drives can be re-added by "
        + "refreshing a registered SharePoint site or re-adding a removed one.",
      icon: "warning",
      showCancelButton: true,
    })
    .then((result) => {
      if (result.isConfirmed) {
        axios.delete(`/api/internal/integrations/msgraph/${settings.id}/onedrive/drives`)
        .then(() => {
          notyf.success("Unused drives removed successfully");
          queryClient.invalidateQueries({ queryKey: ['oneDriveDrives', settings.id] });
        })
        .catch(error => {
          console.error("Error removing unused drives:", error);
          notyf.error("Failed to remove unused drives: " + error.response.data.message);
        });
      }
    })
  }

  if (settingsLoading || sitesLoading || drivesIsLoading) {
    return <LoadingMessageCard message={"Loading Microsoft Azure settings..."} />;
  }

  return (
      <>

        <Row className={"mb-3"}>
            { settings?.active ? (
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
            )}
        </Row>

        <Row>
          <Col>
            { settings?.active ? (
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
            )}
          </Col>
        </Row>

        {/* SharePoint Sites */}
        {settings && settings.active && (
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

              {sites && sites.length > 0 ? sites.map((site, i) => {
                return (
                  <Row key={`site-${i}`}>
                    <Col>
                      <SharePointSiteCard site={site} integration={settings} />
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
              )}

            </>
        )}

        {/* OneDrive */}
        { settings?.active && (
          <>
            <Row className={"mb-3"}>
              <Col>
                <h4>OneDrive Drives</h4>
              </Col>
              <Col xs={"auto"} className={"float-end"}>
                <Dropdown>
                  <Dropdown.Toggle variant="primary">
                    <FontAwesomeIcon icon={faBolt} className={"me-2"} />
                    Actions
                  </Dropdown.Toggle>
                  <Dropdown.Menu>
                    <Dropdown.Item onClick={handleRemoveUnusedDrives}>
                      <FontAwesomeIcon icon={faTrash} className={"me-2"} />
                      Remove Unused Drives
                    </Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </Col>
            </Row>

            { drives && drives.length > 0 ? drives.map((drive, i) => {
              return (
                  <Row key={`drive-${i}`}>
                    <Col>
                      <OneDriveDriveCard drive={drive} integration={settings} />
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
            )}

          </>
        )}

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