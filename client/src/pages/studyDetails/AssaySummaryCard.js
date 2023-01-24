/*
 * Copyright 2023 the original author or authors.
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

import React from "react";
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";
import {StatusBadge} from "../../common/status";

const AssaySummaryCard = ({study, assay}) => {

  return (
    <Card>
      <Card.Body>
        <Row>

          <Col xs={4} className={"d-flex align-items-center"}>
            <div>
              <span className={"text-lg"}>
                <a href={"/study/" + study.code + "/assay/" + assay.code}>
                  {assay.name}
                </a>
              </span>
              <br />
              <span className={"text-muted fw-bolder"}>
                {assay.code}
              </span>
            </div>
          </Col>

          <Col xs={2}>
            <span className="text-muted">Type</span>
            <br />
            <span className={"fw-bolder"}>
              {assay.assayType.name}
            </span>
          </Col>

          <Col xs={2}>
            <span className="text-muted">Status</span>
            <br />
            <StatusBadge status={assay.status} />
          </Col>

          <Col xs={2}>
            <span className="text-muted">Owner</span>
            <br />
            <span className={"fw-bolder"}>
              {assay.owner.displayName}
            </span>
          </Col>

          <Col xs={2}>
            <span className="text-muted">Last Updated</span>
            <br />
            {new Date(assay.updatedAt ? assay.updatedAt : assay.createdAt).toLocaleDateString()}
          </Col>

        </Row>
      </Card.Body>
    </Card>
  );

}

AssaySummaryCard.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired
}

export default AssaySummaryCard;

