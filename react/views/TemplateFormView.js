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

import React, {useMemo} from 'react';
import {history} from '../App';
import swal from 'sweetalert';
import {
  Breadcrumb,
  Button,
  Card,
  Col,
  Container,
  Form,
  Row
} from 'react-bootstrap';
import {Form as FormikForm, Formik} from "formik";
import {LoadingOverlay} from '../components/loading';
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
        <Breadcrumb.Item href="/">Home</Breadcrumb.Item>
        <Breadcrumb.Item href="/admin">Admin Dashboard</Breadcrumb.Item>
        <Breadcrumb.Item active>{ templateFormHeading }</Breadcrumb.Item>
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
          <Card.Header>
            <Card.Title tag="h5">Template Type Details</Card.Title>
            <h6 className="card-subtitle text-muted">
              Template Types must have a unique name and templateId fields
            </h6>
          </Card.Header>
          <Card.Body>
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
              { ({
                errors,
                touched,
                isSubmitting,
                handleChange,
                handleSubmit,
                handleBlur,
                values
              }) => (
                  <React.Fragment>
                    <LoadingOverlay
                      isVisible={isSubmitting}
                      message={'Creating new template...'}
                    />
                    <FormikForm>
                      <Row>
                        <Col xs={12} md={6}>
                          <Form.Control
                            type="text"
                            name="name"
                            value={values.name}
                            onChange={handleChange}
                            placeholder="Enter template name..."
                            invalid={ errors.name && touched.name }
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {errors.name}
                          </Form.Control.Feedback>
                        </Col>
                        <Col xs={12} md={6}>
                          <Form.Control
                            type="text"
                            name="templateId"
                            value={values.templateId}
                            onChange={handleChange}
                            placeholder="Enter template id..."
                            invalid={ errors.templateId && touched.templateId }
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {errors.templateId}
                          </Form.Control.Feedback>
                        </Col>
                      </Row>
    
                      <hr />
    
                      <Row>
                        <Col xs={12}>
                          <div className="text-center">
                            <Button
                              className="mx-1"
                              size="lg"
                              variant="secondary"
                              onClick={handleCancel}
                            >
                              Cancel
                            </Button>
                            <Button
                              className="mx-1"
                              size="lg"
                              variant="primary"
                              type="submit"
                            >
                              Submit
                            </Button>
                          </div>
                        </Col>
                      </Row>
                    </FormikForm>
                  </React.Fragment>
                ) }
            </Formik>
          </Card.Body>
        </Card>
      </Container>
    </NoSidebarPageWrapper>
  )
}