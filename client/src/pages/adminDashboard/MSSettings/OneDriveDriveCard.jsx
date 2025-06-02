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

import {Card, Col, Dropdown, Row} from "react-bootstrap";
import React, { useContext } from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faHardDrive} from "@fortawesome/free-regular-svg-icons";
import {DriveStatusBadge} from "../../../common/fileManager/folderBadges";
import { faCancel, faTrash } from "@fortawesome/free-solid-svg-icons";
import NotyfContext from "@/context/NotyfContext";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios from "axios";

const OneDriveDriveCard = ({drive, integration}) => {

  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();

  const deleteMutation = useMutation({
    mutationFn: () => {
      return axios.delete(`/api/internal/integrations/msgraph/${integration.id}/onedrive/drives/${drive.id}`);
    },
    onSuccess: () => {
      console.log("Drive deleted successfully");
      notyf.success("Drive deleted successfully");
      queryClient.invalidateQueries({ queryKey: ["oneDriveDrives", integration.id] });
    },
    onError: (e) => {
      console.error("Error deleting drive:", e);
      notyf.error("Error deleting drive: " + (e.response?.data?.message || e.message));
    }
  });

  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <FontAwesomeIcon icon={faHardDrive} size={"2x"} className={"text-secondary"}/>
            </Col>

            <Col xs={7} className={"d-flex align-items-center"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  <a href={drive.details.webUrl} target={"_blank"} rel="noopener noreferrer">{drive.displayName}</a>
                </span>
                <br />
                <span className={"text-muted"}>
                  Drive Name: {drive.details.name}
                </span>
                <br />
                <span className={"text-muted"}>
                  Drive ID: <code>{drive.details.driveId}</code>
                </span>
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Status</span>
                <br />
                <DriveStatusBadge active={drive.active} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <Dropdown>
                <Dropdown.Toggle variant="outline-primary">
                  Actions
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  {/*{*/}
                  {/*    drive.active && (*/}
                  {/*        <Dropdown.Item onClick={() => console.log("Click")}>*/}
                  {/*          <FontAwesomeIcon icon={faCancel} className={"me-1"} />*/}
                  {/*          Set Inactive*/}
                  {/*        </Dropdown.Item>*/}
                  {/*    )*/}
                  {/*}*/}

                  {/*{*/}
                  {/*    !drive.active && (*/}
                  {/*        <Dropdown.Item onClick={() => console.log("Click")}>*/}
                  {/*          <FontAwesomeIcon icon={faCheckCircle} className={"me-1"} />*/}
                  {/*          Set Active*/}
                  {/*        </Dropdown.Item>*/}
                  {/*    )*/}
                  {/*}*/}

                  <Dropdown.Item onClick={() => deleteMutation.mutate()}>
                    <FontAwesomeIcon icon={faTrash} className={"me-1"} />
                    Remove Drive
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

OneDriveDriveCard.propTypes = {
  drive: PropTypes.object.isRequired,
  integration: PropTypes.object.isRequired,
}

export default OneDriveDriveCard;