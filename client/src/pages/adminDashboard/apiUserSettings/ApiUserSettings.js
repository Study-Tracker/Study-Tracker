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

import {Button, Card, Col, Row} from "react-bootstrap";
import {UserPlus} from "react-feather";
import React, {useEffect, useRef, useState} from "react";
import axios from "axios";
import ApiUserTable from "./ApiUserTable";
import ApiUserFormModal from "./ApiUserFormModal";
import swal from "sweetalert";

const ApiUserSettings = () => {

  const [apiUsers, setApiUsers] = useState([]);
  const [counter, setCounter] = useState(0);
  const [error, setError] = useState(null);
  const [formModalIsOpen, setFormModalIsOpen] = useState(false);
  const [selectedApiUser, setSelectedApiUser] = useState(null);
  const formikRef = useRef();

  useEffect(() => {
    axios.get("/api/internal/user?type=API_USER")
    .then(async response => {
      setApiUsers(response.data);
    })
    .catch(error => {
      console.error(error);
      setError(error);
    });
  }, [counter]);

  const handleFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("Form values", values);
    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/internal/user/" + values.id
        : "/api/internal/user";

    axios({
      url: url,
      method: isUpdate ? "put" : "post",
      data: values
    })
    .then(response => {
      const json = response.data;
      console.debug("User", json);
      resetForm();
    })
    .catch(e => {
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    })
    .finally(() => {
      setSubmitting(false);
      setFormModalIsOpen(false);
      setCounter(counter + 1);
    });

  }

  const handleUserEdit = (apiUser) => {
    formikRef.current?.resetForm();
    setSelectedApiUser(apiUser);
    setFormModalIsOpen(true);
  }

  const triggerRefresh = () => {
    setCounter(counter + 1);
  }

  return (
      <React.Fragment>

        <Card>
          <Card.Header>
            <Card.Title tag="h5" className="mb-0">
              API Users
              <span className="float-end">
                <Button
                    color={"primary"}
                    onClick={() => {
                      formikRef.current?.resetForm();
                      setSelectedApiUser(null);
                      setFormModalIsOpen(true);
                    }}
                >
                  New API User
                  &nbsp;
                  <UserPlus className="feather align-middle ms-2 mb-1"/>
                </Button>
              </span>
            </Card.Title>
          </Card.Header>
          <Card.Body>

            <Row>
              <Col>
                <div className="info-alert">
                  Register API user accounts for use with the Study Tracker REST API. API users cannot
                  log into the Study Tracker web application. API users are assigned a unique API key,
                  which is used to authenticate API requests.
                </div>
              </Col>
            </Row>

            <Row>
              <Col>
                <ApiUserTable
                    users={apiUsers}
                    handleUserUpdate={handleUserEdit}
                    triggerRefresh={triggerRefresh}
                />
              </Col>
            </Row>


          </Card.Body>
        </Card>

        <ApiUserFormModal
            modalIsOpen={formModalIsOpen}
            setModalIsOpen={setFormModalIsOpen}
            user={selectedApiUser}
            handleFormSubmit={handleFormSubmit}
            formikRef={formikRef}
        />

      </React.Fragment>
  )
}

export default ApiUserSettings;
