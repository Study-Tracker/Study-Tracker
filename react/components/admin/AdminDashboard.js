import React, {useEffect, useState} from 'react';
import {Link, useLocation} from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardHeader,
  CardTitle,
  Col,
  Container,
  ListGroup,
  ListGroupItem,
  Row
} from "reactstrap";
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
        <ListGroupItem
            key={'setting-tab-' + s.id}
            action
            active={activeTab === s.id}
            onClick={() => onSetActiveTab(s.id)}
        >
          {s.label}
        </ListGroupItem>
    )
  })

  return (
      <Container fluid>
        <Row>
          <Col>
            <Breadcrumb>
              <BreadcrumbItem>
                <Link to="/">Home</Link>
              </BreadcrumbItem>
              <BreadcrumbItem active>Admin Dashboard</BreadcrumbItem>
            </Breadcrumb>
        </Col>
      </Row>

      <Row className="justify-content-between align-items-center">
        <Col>
          <h1>Admin Dashboard</h1>
        </Col>
      </Row>

      <Row>
        <Col md="3" xl="2">
          <Card>
            <CardHeader>
              <CardTitle tag="h5" className="mb-0">
                Site Settings
              </CardTitle>
            </CardHeader>
            <ListGroup flush>
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
