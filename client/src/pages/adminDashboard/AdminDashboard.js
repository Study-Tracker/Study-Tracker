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
import UserSettings from "./userSettings/UserSettings";
import AssayTypeSettings from "./AssayTypeSettings";
import KeywordSettings from "./KeywordSettings";
import ProgramSettings from "./ProgramSettings";
import AdminDashboardPlaceholder from "./AdminDashboardPlaceholder";
import ApiUserSettings from "./apiUserSettings/ApiUserSettings";
import BenchlingIntegrationSettings from "./BenchlingIntegrationSettings";
import EgnyteIntegrationSettings
  from "./egnyteIntegrationSettings/EgnyteIntegrationSettings";
import GitlabIntegrationSettings from "./GitlabIntegrationSettings";
import StorageFolderSettings
  from "./storageFolderSettings/StorageFolderSettings";
import AWSIntegrationSettings from "./AWSSettings/AWSIntegrationSettings";

const settings = {
  "users": {
    id: "users",
    category: "site-settings",
    label: "Users",
    tag: UserSettings
  },
  "assay-types": {
    id: "assay-types",
    category: "site-settings",
    label: "Assay Types",
    tag: AssayTypeSettings
  },
  "keywords": {
    id: "keywords",
    category: "site-settings",
    label: "Keywords",
    tag: KeywordSettings
  },
  "programs": {
    id: "programs",
    category: "site-settings",
    label: "Programs",
    tag: ProgramSettings
  },
  "storage-folders": {
    id: "storage-folders",
    category: "site-settings",
    label: "Storage Folders",
    tag: StorageFolderSettings
  },

  // Integrations
  "aws": {
    id: "aws",
    category: "integrations",
    label: "Amazon Web Services",
    tag: AWSIntegrationSettings
  },
  "benchling": {
    id: "benchling",
    category: "integrations",
    label: "Benchling",
    tag: BenchlingIntegrationSettings
  },
  "egnyte": {
    id: "egnyte",
    category: "integrations",
    label: "Egnyte",
    tag: EgnyteIntegrationSettings
  },
  "gitlab": {
    id: "gitlab",
    category: "integrations",
    label: "GitLab",
    tag: GitlabIntegrationSettings
  },

  // Developer Settings
  "api-documentation": {
    id: "api-documentation",
    category: "developer",
    label: "API Documentation",
    onClick: () => window.open("/swagger-ui.html", "_blank")
  },
  "api-users": {
    id: "api-users",
    category: "developer",
    label: "API Users",
    tag: ApiUserSettings
  }
};

const useQuery = () => {
  return new URLSearchParams(useLocation().search);
}

const AdminDashboard = () => {

  const location = useLocation();
  const [activeTab, setActiveTab] = useState(null);
  const activeTabQuery = useQuery().get('active');
  const navigate = useNavigate();

  useEffect(() => {
    if (activeTabQuery
        && Object.keys(settings).includes(activeTabQuery)
        && activeTab !== activeTabQuery) {
      setActiveTab(activeTabQuery);
    }
  }, [location]);

  const getDashboardContent = () => {
    if (!!activeTab) {
      const Tag = settings[activeTab].tag;
      return <Tag/>;
    } else {
      return <AdminDashboardPlaceholder />;
    }
  }

  const onSetActiveTab = (tabName) => {
    navigate('/admin?active=' + tabName);
  }

  const renderMenu = (category) => {
    return Object.values(settings)
    .filter((setting) => setting.category === category)
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
              onClick={s.onClick || (() => onSetActiveTab(s.id))}
          >
            {s.label}
          </ListGroup.Item>
      )
    })
  }

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
                {renderMenu('site-settings')}
              </ListGroup>
            </Card>

            <Card>
              <Card.Header>
                <Card.Title tag="h5" className="mb-0">
                  Integrations
                </Card.Title>
              </Card.Header>
              <ListGroup variant={"flush"}>
                {renderMenu('integrations')}
              </ListGroup>
            </Card>

            <Card>
              <Card.Header>
                <Card.Title tag="h5" className="mb-0">
                  Developer Tools
                </Card.Title>
              </Card.Header>
              <ListGroup variant={"flush"}>
                {renderMenu('developer')}
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
