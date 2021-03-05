import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
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
import { history } from '../../App';

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export const AdminDashboard = () => {
  const location = useLocation();
  const [activeTab, setActiveTab] = useState(null);
  const activeTabQuery = useQuery().get('active');

  useEffect(() => {
    if ( ['users', 'assay-types', 'template-types'].includes(activeTabQuery) && activeTab !== activeTabQuery ) {
      setActiveTab(activeTabQuery);
    }
    else {
      setActiveTab('users');
    }
  }, [location]);

  const getDashboardContent = () => {
    switch(activeTab) {
      case 'users':
        return <UserSettings />
      case 'assay-types':
        return <AssayTypeSettings />
      case 'template-types':
        return <TemplateTypesSettings />
    }
  }

  const onSetActiveTab = (tabName) => {
    history.push({
      pathname: '/admin',
      search: `?active=${ tabName }`,
    });
  }

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
              <ListGroupItem
                action
                active={activeTab === 'users'}
                onClick={() => onSetActiveTab('users')}
              >
                Users
              </ListGroupItem>
              <ListGroupItem
                action
                active={activeTab === 'assay-types'}
                onClick={() => onSetActiveTab('assay-types')}
              >
                Assay Types
              </ListGroupItem>
              <ListGroupItem
                action
                active={activeTab === 'template-types'}
                onClick={() => onSetActiveTab('template-types')}
              >
                ELN Entry Templates
              </ListGroupItem>
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
