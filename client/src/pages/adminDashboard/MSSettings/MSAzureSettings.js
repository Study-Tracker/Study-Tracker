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

const MSAzureSettings = () => {

  const [settings, setSettings] = useState(null);
  const [drives, setDrives] = useState([]);
  const [sites, setSites] = useState([]);
  const [loadCount, setLoadCount] = useState(0);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [sharePointSiteModalIsOpen, setSharePointSiteModalIsOpen] = useState(false);
  const notyf = useContext(NotyfContext);
  const formikRef = useRef();

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
        message: "Failed to save Microsoft Azure integration settings"
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  return (
      <>

        <Row className={"mb-3 justify-content-around"}>
          <Col>
            <h3>
              Microsoft Azure integration is
              &nbsp;
              {
                settings && settings.active ? (
                    <span className={"text-success"}>ENABLED</span>
                ) : (
                    <span className={"text-secondary"}>DISABLED</span>
                )
              }
            </h3>
          </Col>
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

        {
          settings && settings.active && (
                <Row className={"mb-3 justify-content-around"}>
                  <Col>
                    <h4>SharePoint Sites</h4>
                  </Col>
                  <Col>
                    <Button
                      color={"primary"}
                      onClick={() => setSharePointSiteModalIsOpen(true)}
                    >
                      Add SharePoint Site
                    </Button>
                  </Col>
                </Row>
          )
        }

        <MSGraphIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={formikRef}
        />

      </>
  )

}

export default MSAzureSettings;