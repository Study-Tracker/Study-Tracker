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
import {Col, Dropdown, Row} from "react-bootstrap";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGears, faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {
  faCheckCircle,
  faCircleXmark,
  faEdit
} from "@fortawesome/free-regular-svg-icons";
import BenchlingIntegrationSetupCard from "./BenchlingIntegrationSetupCard";
import BenchlingIntegrationFormModal from "./BenchlingIntegrationFormModal";
import BenchlingIntegrationDetailsCard from "./BenchlingIntegrationDetailsCard";
import {CardLoadingMessage} from "../../../common/loading";
import {CardErrorMessage} from "../../../common/errors";

const BenchlingIntegrationSettings = () => {

  const notyf = useContext(NotyfContext);
  const integrationFormikRef = useRef();
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const queryClient = useQueryClient();

  const {data: settings, isLoading, error} = useQuery({
    queryKey: ["benchlingSettings"],
    queryFn: async () => {
      return axios.get("/api/internal/integrations/benchling")
      .then(response => {
        console.debug("Benchling settings loaded", response.data);
        if (response.data.length === 0) {
          console.warn("No Benchling settings found");
          return null;
        } else if (response.data.length === 1) {
          return response.data[0];
        } else {
          console.warn("Multiple Benchling settings found", response.data);
          return response.data[0];
        }
      })
    }
  });

  const changeStatusMutation = useMutation({
    mutationFn: (active) => {
      return axios.patch("/api/internal/integrations/benchling/" + settings.id + "?active=" + active)
    },
    onSuccess: () => {
      notyf.open({
        type: "success",
        message: "Benchling integration settings saved"
      });
      queryClient.invalidateQueries({queryKey: ["benchlingSettings"]});
    },
    onError: (error) => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save Benchling integration settings: " + error.response.data.message
      });
    }
  });

  return (
      <>

        <Row className={"mb-3"}>
          <Col className={"d-flex justify-content-between"}>

            <div>
              <h3>
                Benchling integration is
                &nbsp;
                {
                  settings && settings.active ? (
                    <span className={"text-success"}>ENABLED</span>
                  ) : (
                    <span className={"text-muted"}>DISABLED</span>
                  )
                }
              </h3>
            </div>

            <div>
              <Dropdown>

                <Dropdown.Toggle variant={"primary"} id="dropdown-basic">
                  <FontAwesomeIcon icon={faGears} className={"me-2"}/>
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
                        <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                        Edit Registration
                      </Dropdown.Item>
                    ) : (
                      <Dropdown.Item onClick={() => {
                        setIntegrationModalIsOpen(true);
                      }}>
                        <FontAwesomeIcon icon={faPlusCircle} className={"me-2"}/>
                        Add Registration
                      </Dropdown.Item>
                    )
                  }

                  {
                    settings && settings.active ? (
                      <Dropdown.Item onClick={() => changeStatusMutation.mutate(false)}>
                        <FontAwesomeIcon icon={faCircleXmark} className={"me-2"}/>
                        Disable Integration
                      </Dropdown.Item>
                    ) : settings && !settings.active ? (
                      <Dropdown.Item onClick={() => changeStatusMutation.mutate(true)}>
                        <FontAwesomeIcon icon={faCheckCircle} className={"me-2"}/>
                        Re-enable Integration
                      </Dropdown.Item>
                    ) : ""
                  }

                </Dropdown.Menu>

              </Dropdown>
            </div>

          </Col>
        </Row>

        {
          isLoading ? (
            <CardLoadingMessage message={"Loading Benchling integration settings..."} />
          ) : error ? (
            <CardErrorMessage message={error.message} />
          ) : settings ? (
            <BenchlingIntegrationDetailsCard settings={settings} />
          ) : (
            <BenchlingIntegrationSetupCard handleClick={() => setIntegrationModalIsOpen(true)} />
          )
        }

        <BenchlingIntegrationFormModal
          isOpen={integrationModalIsOpen}
          setIsOpen={setIntegrationModalIsOpen}
          formikRef={integrationFormikRef}
          selectedIntegration={settings}
        />

      </>
  )
}

export default BenchlingIntegrationSettings;