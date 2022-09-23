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

import React, {useEffect, useState} from "react";
import {useLocation, useNavigate} from 'react-router-dom';
import {
  Breadcrumb,
  Card,
  Col,
  Container,
  ListGroup,
  Row
} from "react-bootstrap";
import UserSettings from "./UserSettings";
import AssayTypeSettings from "./AssayTypeSettings";
import KeywordSettings from "./KeywordSettings";
import ProgramSettings from "./ProgramSettings";

const settings = {
  "users": {
    id: "users",
    label: "Users",
    tag: UserSettings
  },
  "assay-types": {
    id: "assay-types",
    label: "Assay Types",
    tag: AssayTypeSettings
  },
  "keywords": {
    id: "keywords",
    label: "Keywords",
    tag: KeywordSettings
  },
  "programs": {
    id: "programs",
    label: "Programs",
    tag: ProgramSettings
  }
};

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export const AdminDashboard = props => {

  const location = useLocation();
  const [activeTab, setActiveTab] = useState(null);
  const activeTabQuery = useQuery().get('active');
  const navigate = useNavigate();

  useEffect(() => {
    if (Object.keys(settings).includes(activeTabQuery) && activeTab
        !== activeTabQuery) {
      setActiveTab(activeTabQuery);
    } else {
      setActiveTab(Object.keys(settings).sort()[0]);
    }
  }, [location]);

  const getDashboardContent = () => {
    if (!!activeTab) {
      const Tag = settings[activeTab].tag;
      return <Tag/>;
    } else {
      return '';
    }
  }

  const onSetActiveTab = (tabName) => {
    navigate('/admin?active=' + tabName);
  }

  const controls = Object.values(settings)
  .sort((a, b) => {
    if (a.label > b.label) {
      return 1;
    } else if (a.label < b.label) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(s => {
    return (
        <ListGroup.Item
            key={'setting-tab-' + s.id}
            action
            active={activeTab === s.id}
            onClick={() => onSetActiveTab(s.id)}
        >
          {s.label}
        </ListGroup.Item>
    )
  })

  return (
      <Container fluid>
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
              <Breadcrumb.Item active>Admin Dashboard</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        <Row className="justify-content-between align-items-center">
          <Col>
            <h3>Admin Dashboard</h3>
          </Col>
        </Row>

        <Row>
          <Col md="3" xl="2">

            <Card>
              <Card.Header>
                <Card.Title tag="h5" className="mb-0">
                  Site Settings
                </Card.Title>
              </Card.Header>
              <ListGroup variant={"flush"}>
                {controls}
              </ListGroup>
            </Card>

            <Card>
              <Card.Header>
                <Card.Title tag="h5" className="mb-0">
                  Developer Tools
                </Card.Title>
              </Card.Header>
              <ListGroup variant={"flush"}>
                <ListGroup.Item
                    action
                    href={"/swagger-ui.html"}
                >
                  API Documentation
                </ListGroup.Item>
              </ListGroup>
            </Card>

          </Col>

          <Col md="9" xl="10">
            {getDashboardContent()}
          </Col>
        </Row>
      </Container>
  );
}

export default AdminDashboard;
