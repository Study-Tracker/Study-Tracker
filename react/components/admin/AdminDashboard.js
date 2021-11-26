import React, {useEffect, useState} from 'react';
import {useLocation} from 'react-router-dom';
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
import TemplateTypesSettings from './TemplateTypesSettings';
import {history} from '../../App';
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
  "eln-entry-templates": {
    id: "eln-entry-templates",
    label: "ELN Entry Templates",
    tag: TemplateTypesSettings
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

export const AdminDashboard = () => {
  const location = useLocation();
  const [activeTab, setActiveTab] = useState(null);
  const activeTabQuery = useQuery().get('active');

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
    history.push({
      pathname: '/admin',
      search: `?active=${tabName}`,
    });
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
        </Col>

        <Col md="9" xl="10">
          { getDashboardContent() }
        </Col>
      </Row>
    </Container>
  );
}

export default AdminDashboard;
