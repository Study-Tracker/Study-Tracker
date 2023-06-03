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

import {Breadcrumb, Col, Container, Row, Tab, Tabs} from "react-bootstrap";
import React, {useState} from "react";
import ProgramTimelineTab from "./ProgramTimelineTab";
import ProgramStudiesTab from "./ProgramStudiesTab";
import PropTypes from "prop-types";
import {useLocation, useNavigate} from "react-router-dom";
import ProgramDetailHeader from "./ProgramDetailsHeader";
import ProgramOverviewTab from "./ProgramOverviewTab";

const ProgramDetails = ({program, studies, user}) => {

  const location = useLocation();
  const [selectedTab, setSelectedTab] = useState(location.hash.replace("#", "") || "overview");
  const navigate = useNavigate();

  const handleTabSelect = (key) => {
    setSelectedTab(key);
    navigate("#" + key);
  }

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/programs"}>Programs</Breadcrumb.Item>
              <Breadcrumb.Item active>{program.name}</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <ProgramDetailHeader program={program} user={user}/>

        <Row>

          <Col lg={12}>

            {/* Tabs */}

            <Tabs variant={"pills"} activeKey={selectedTab} onSelect={handleTabSelect}>

              <Tab eventKey={"overview"} title={"Overview"}>
                <ProgramOverviewTab program={program} />
              </Tab>

              <Tab eventKey={"studies"} title={"Studies"}>
                <ProgramStudiesTab studies={studies} user={user}/>
              </Tab>

              <Tab eventKey={"timeline"} title={"Timeline"}>
                <ProgramTimelineTab program={program} user={user}/>
              </Tab>

            </Tabs>

          </Col>

        </Row>
      </Container>
  );

}

ProgramDetails.propTypes = {
  program: PropTypes.object.isRequired,
  studies: PropTypes.array.isRequired,
  user: PropTypes.object
}

export default ProgramDetails;