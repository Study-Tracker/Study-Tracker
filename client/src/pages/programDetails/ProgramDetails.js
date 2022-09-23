/*
 * Copyright 2022 the original author or authors.
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

import {
  Breadcrumb,
  Card,
  Col,
  Container,
  Dropdown,
  Nav,
  Row,
  Tab
} from "react-bootstrap";
import React from "react";
import {Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import {
  ProgramStatusButton,
  SelectableProgramStatusButton
} from "./programStatus";
import ProgramTimelineTab from "./ProgramTimelineTab";
import ProgramStudiesTab from "./ProgramStudiesTab";
import {
  ModelDetailsAttributeList
} from "../../common/ModelDetailsAttributeList";
import PropTypes from "prop-types";

const ProgramDetailHeader = ({program, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>Program {program.name} ({program.code})</h3>
        </Col>
        <Col className="col-auto">
          {
            !!user && !!user.admin
                ? <SelectableProgramStatusButton active={program.active}
                                                 programId={program.id}/>
                : <ProgramStatusButton active={program.active}/>

          }

        </Col>
      </Row>
  );
};

const ProgramDetails = props => {

  const {program, studies, user} = props;
  const createMarkup = (content) => {
    return {__html: content};
  };

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/programs"}>Programs</Breadcrumb.Item>
              <Breadcrumb.Item active>Program Detail</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <ProgramDetailHeader program={program} user={user}/>

        <Row>

          <Col lg={5}>
            <Card className="details-card">

              <Card.Header>
                <div className="card-actions float-end">
                  <Dropdown align="end">
                    <Dropdown.Toggle tag="a" bsPrefix="-">
                      <Menu/>
                    </Dropdown.Toggle>
                    <Dropdown.Menu>

                      {
                        !!user && !!user.admin ? (
                            <Dropdown.Item
                                href={"/program/" + program.id + "/edit"}>
                              <FontAwesomeIcon icon={faEdit}/>
                              &nbsp;
                              Edit
                            </Dropdown.Item>
                        ) : ''
                      }

                    </Dropdown.Menu>
                  </Dropdown>
                </div>
                <Card.Title tag="h5" className="mb-0 text-muted">
                  Summary
                </Card.Title>
              </Card.Header>

              <Card.Body>
                <Row>
                  <Col xs={12}>

                    <h3>{program.name}</h3>

                    <h6 className="details-label">Description</h6>
                    <div dangerouslySetInnerHTML={
                      createMarkup(program.description)
                    }/>

                    <h6 className="details-label">Created By</h6>
                    <p>{program.createdBy.displayName}</p>

                    <h6 className="details-label">Last Updated</h6>
                    <p>{new Date(program.updatedAt).toLocaleString()}</p>

                    <ModelDetailsAttributeList
                        attributes={program.attributes}/>

                  </Col>
                </Row>
              </Card.Body>

            </Card>
          </Col>

          <Col lg={7}>

            {/* Tabs */}
            <div className="tab">
              <Tab.Container defaultActiveKey={"timeline"}>
                <Nav variant={"tabs"}>

                  <Nav.Item>
                    <Nav.Link eventKey={"timeline"}>
                      Timeline
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"studies"}>
                      Studies
                    </Nav.Link>
                  </Nav.Item>

                </Nav>

                <Tab.Content>

                  <Tab.Pane eventKey={"timeline"}>
                    <ProgramTimelineTab program={program} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"studies"}>
                    <ProgramStudiesTab studies={studies} user={user}/>
                  </Tab.Pane>

                </Tab.Content>
              </Tab.Container>
            </div>
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