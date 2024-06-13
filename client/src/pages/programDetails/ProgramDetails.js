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
import React from "react";
import ProgramTimelineTab from "./ProgramTimelineTab";
import ProgramStudiesTab from "./ProgramStudiesTab";
import PropTypes from "prop-types";
import {useLocation, useNavigate} from "react-router-dom";
import ProgramDetailHeader from "./ProgramDetailsHeader";
import ProgramOverviewTab from "./ProgramOverviewTab";
import {useDispatch, useSelector} from "react-redux";
import {setTab} from "../../redux/tabSlice";
import ProgramFileManagerTab from "./ProgramFileManagerTab";
import ProgramTeamTab from "./ProgramTeamTab";

const ProgramDetails = ({program, user}) => {

  const location = useLocation();
  const navigate = useNavigate();
  const tab = useSelector(s => s.tab.value) || location.hash.replace("#", "") || "overview";
  const dispatch = useDispatch();

  const handleTabSelect = (key) => {
    dispatch(setTab(key));
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

            <Tabs variant={"pills"} activeKey={tab} onSelect={handleTabSelect}>

              <Tab eventKey={"overview"} title={"Overview"}>
                <ProgramOverviewTab program={program} />
              </Tab>

              <Tab eventKey={"team"} title={"Team"}>
                <ProgramTeamTab program={program} />
              </Tab>

              <Tab eventKey={"studies"} title={"Studies"}>
                <ProgramStudiesTab program={program} />
              </Tab>

              <Tab eventKey={"timeline"} title={"Timeline"}>
                <ProgramTimelineTab program={program} user={user}/>
              </Tab>

              <Tab eventKey={"files"} title={"Files"}>
                <ProgramFileManagerTab program={program} />
              </Tab>

            </Tabs>

          </Col>

        </Row>
      </Container>
  );

}

ProgramDetails.propTypes = {
  program: PropTypes.object.isRequired,
  user: PropTypes.object
}

export default ProgramDetails;