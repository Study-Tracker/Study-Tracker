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

import React, { useMemo } from 'react';
import { history } from '../App';
import swal from 'sweetalert';
import {
  Container,
  Row,
  Col,
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  FormFeedback,
  Button,
  Input,
} from 'reactstrap';
import { Field, Form, Formik } from "formik";
import { Link } from 'react-router-dom';
import { LoadingOverlay } from '../components/loading';
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";

export const TemplateFormView = (props) => {
  const templateTypeId = props.match.params.templateTypeId;

  const templateFormHeading = useMemo(() => {
    return templateTypeId
      ? 'Edit Template'
      : 'Create Template';
  }, [templateTypeId]);

  const templateFormBreadcrumbs = useMemo(() => {
    return (
      <Breadcrumb>
        <BreadcrumbItem>
          <Link to="/">Home</Link>
        </BreadcrumbItem>
        <BreadcrumbItem>
          <Link to="/admin">Admin Dashboard</Link>
        </BreadcrumbItem>
        <BreadcrumbItem active>{ templateFormHeading }</BreadcrumbItem>
      </Breadcrumb>
    );
  }, [templateFormHeading]);

  const handleCancel = () => {
    history.push('/admin?active=template-types')
  }

  return (
    <NoSidebarPageWrapper>
      <Container
        className="max-width-1200"
        fluid
      >
        { templateFormBreadcrumbs }
        <h1>
          { templateFormHeading }
        </h1>

        <Card>
          <CardHeader>
            <CardTitle tag="h5">Template Type Details</CardTitle>
            <h6 className="card-subtitle text-muted">
              Template Types must have a unique name and templateId fields
            </h6>
          </CardHeader>
          <CardBody>
            <Formik
              initialValues={ { name: '', templateId: '' } }
              validate={ values => {
                const errors = {};

                if (!values.name) { errors.name = 'Required field'; }
                if (!values.templateId) { errors.templateId = 'Required field'; }

                return errors;
              } }
              onSubmit={ (values,{ setSubmitting } ) => {
                fetch('/api/entryTemplate', {
                  method: 'POST',
                  headers: {
                    'Content-Type': 'application/json'
                  },
                  body: JSON.stringify(values),
                })
                  .then(async response => {
                    if (response.ok) {
                      setTimeout(() => {
                        history.push('/admin?active=template-types');
                      }, 1000);
                    }
                    else {
                      const json = await response.json();
                      swal('Something went wrong',
                        json.message
                          ? 'Error: ' + json.message
                          : 'The request failed. Please contact Study Tracker support.'
                      );
                      setSubmitting(false);
                    }
                  })
                  .catch(error => {
                    swal(
                      'Something went wrong',
                      'The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support.'
                    );
                    console.log(error);
                    setSubmitting(false);
                  })
              } }
            >
              { ({errors, touched, isSubmitting}) => (
                  <>
                    <LoadingOverlay
                      isVisible={isSubmitting}
                      message={'Creating new template...'}
                    />
                    <Form>
                      <Row form>
                        <Col xs="12" md="6">
                          <Input
                            type="text"
                            name="name"
                            tag={Field}
                            placeholder="Enter template name..."
                            invalid={ errors.name && touched.name }
                          />
                          <FormFeedback>{errors.name}</FormFeedback>
                        </Col>
                        <Col xs="12" md="6">
                          <Input
                            type="text"
                            name="templateId"
                            tag={Field}
                            placeholder="Enter template id..."
                            invalid={ errors.templateId && touched.templateId }
                          />
                          <FormFeedback>{errors.templateId}</FormFeedback>
                        </Col>
                      </Row>
    
                      <hr />
    
                      <Row form>
                        <Col xs="12">
                          <div className="text-center">
                            <Button
                              className="mx-1"
                              size="lg"
                              color="secondary"
                              onClick={handleCancel}
                            >
                              Cancel
                            </Button>
                            <Button
                              className="mx-1"
                              size="lg"
                              color="primary"
                              type="submit"
                            >
                              Submit
                            </Button>
                          </div>
                        </Col>
                      </Row>
                    </Form>
                  </>
                ) }
            </Formik>
          </CardBody>
        </Card>
      </Container>
    </NoSidebarPageWrapper>
  )
}