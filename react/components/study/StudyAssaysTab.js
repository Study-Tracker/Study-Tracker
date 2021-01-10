/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Button, Col, Row} from "reactstrap";
import {history} from "../../App";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {AssaySummaryCards} from "../assays";
import React from "react";

const StudyAssaysTab = ({study, user}) => {

  return (
      <div>
        <Row className="justify-content-between align-items-center mb-4">
          <Col>
            {
              !!user
                  ? (
                      <span className="float-right">
                        <Button color="info" onClick={() => history.push(
                            "/study/" + study.code + "/assays/new")}>
                          New Assay
                          &nbsp;
                          <FontAwesomeIcon icon={faPlusCircle}/>
                        </Button>
                      </span>
                  ) : ''
            }
          </Col>
        </Row>

        <AssaySummaryCards studyCode={study.code}/>
      </div>
  );

};

export default StudyAssaysTab;