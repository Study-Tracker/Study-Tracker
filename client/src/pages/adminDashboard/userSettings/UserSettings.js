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

import React, {useEffect} from "react";
import {Button, Card, Col, Row} from 'react-bootstrap';
import {UserPlus} from 'react-feather';
import {SettingsErrorMessage} from "../../../common/errors";
import {SettingsLoadingMessage} from "../../../common/loading";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import UserSettingsTable from "./UserSettingsTable";
import UserDetailsModal from "./UserDetailsModal";

const UserSettings = () => {

  const [state, setState] = React.useState({
    users: [],
    isLoaded: false,
    isError: false,
    showDetails: false,
    selectedUser: null
  });
  const [isModalOpen, setIsModalOpen] = React.useState(false);
  const navigate = useNavigate();

  const showModal = (selected) => {
    if (selected) {
      setState(prevState => ({...prevState, selectedUser: selected}));
      setIsModalOpen(true)
    } else {
      setIsModalOpen(false);
    }
  }

  useEffect(() => {
    axios.get("/api/internal/user")
    .then(async response => {
      setState(prevState => ({
        ...prevState,
        users: response.data,
        isLoaded: true
      }));
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });
  }, []);

  let content = '';
  if (state.isLoaded) {
    content = <UserSettingsTable users={state.users} showModal={showModal}/>
  } else if (state.isError) {
    content = <SettingsErrorMessage/>
  } else {
    content = <SettingsLoadingMessage/>
  }

  return (
      <React.Fragment>

        <Card>
          <Card.Header>
            <Card.Title tag="h5" className="mb-0">
              Registered Users
              <span className="float-end">
                <Button
                    color={"primary"}
                    onClick={() => navigate("/users/new")}
                >
                  New User
                  &nbsp;
                  <UserPlus className="feather align-middle ms-2 mb-1"/>
                </Button>
              </span>
            </Card.Title>
          </Card.Header>
          <Card.Body>

            <Row>
              <Col>
                {content}
              </Col>
            </Row>

            <UserDetailsModal
                showModal={showModal}
                isOpen={isModalOpen}
                user={state.selectedUser}
            />

          </Card.Body>
        </Card>

      </React.Fragment>
  );

}

export default UserSettings;