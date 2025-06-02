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
import {DriveStatusBadge} from "../../../common/fileManager/folderBadges";
import {
  faArrowRotateRight,
  faSitemap, faTrash
} from "@fortawesome/free-solid-svg-icons";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import NotyfContext from "@/context/NotyfContext";

const SharePointSiteCard = ({site, integration}) => {

  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();

  const refreshMutation = useMutation({
    mutationFn: () => {
      return axios.post(`/api/internal/integrations/msgraph/${integration.id}/sharepoint/sites/${site.id}/refresh`);
    },
    onSuccess: () => {
      console.log("Site refreshed successfully");
      notyf.success("Site refreshed successfully");
      queryClient.invalidateQueries({ queryKey: ["sharepointSites", integration.id] });
    },
    onError: (e) => {
      console.error("Error refreshing site:", e);
      notyf.error("Error refreshing site: " + (e.response?.data?.message || e.message));
    }
  });

  const deleteMutation = useMutation({
    mutationFn: () => {
      return axios.delete(`/api/internal/integrations/msgraph/${integration.id}/sharepoint/sites/${site.id}`);
    },
    onSuccess: () => {
      console.log("Site deleted successfully");
      notyf.success("Site deleted successfully");
      queryClient.invalidateQueries({ queryKey: ["sharepointSites", integration.id] });
    },
    onError: (e) => {
      console.error("Error deleting site:", e);
      notyf.error("Error deleting site: " + (e.response?.data?.message || e.message));
    }
  });

  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <FontAwesomeIcon icon={faSitemap} size={"2x"} className={"text-secondary"}/>
            </Col>

            <Col xs={7} className={"d-flex align-items-center"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  <a href={site.url} target={"_blank"} rel="noopener noreferrer">{site.name}</a>
                </span>
                <br />
                <span className={"text-muted"}>
                  Site ID: <code>{site.siteId}</code>
                </span>
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Status</span>
                <br />
                <DriveStatusBadge active={site.active} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <Dropdown>
                <Dropdown.Toggle variant="outline-primary">
                  Actions
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => refreshMutation.mutate()}>
                    <FontAwesomeIcon icon={faArrowRotateRight} className={"me-1"} />
                    Refresh Site Drives
                  </Dropdown.Item>

                  <Dropdown.Item onClick={() => deleteMutation.mutate()}>
                    <FontAwesomeIcon icon={faTrash} className={"me-1"} />
                    Remove Site
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

SharePointSiteCard.propTypes = {
  site: PropTypes.object.isRequired,
  integration: PropTypes.object.isRequired,
}

export default SharePointSiteCard;