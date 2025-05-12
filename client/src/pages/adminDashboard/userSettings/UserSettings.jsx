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

import React from "react";
import {Button, Card, Col, Row} from 'react-bootstrap';
import {UserPlus} from 'react-feather';
import {SettingsErrorMessage} from "../../../common/errors";
import {LoadingMessageCard} from "../../../common/loading";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import UserSettingsTable from "./UserSettingsTable";
import UserDetailsModal from "./UserDetailsModal";
import {useQuery} from "@tanstack/react-query";

const UserSettings = () => {

  const [selectedUser, setSelectedUser] = React.useState(null);
  const [isModalOpen, setIsModalOpen] = React.useState(false);
  const navigate = useNavigate();

  const showModal = (selected) => {
    if (selected) {
      setSelectedUser(selected);
      setIsModalOpen(true)
    } else {
      setIsModalOpen(false);
    }
  }

  const {data: users, isLoading, error} = useQuery({
    queryKey: ["users"],
    queryFn: () => {
      return axios.get("/api/internal/user").then(response => response.data);
    }
  });

  if (isLoading) return <LoadingMessageCard />
  if (error) return <SettingsErrorMessage error={error} />

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
                {
                  users && (
                      <UserSettingsTable users={users} showModal={showModal}/>
                    )
                }
              </Col>
            </Row>

            <UserDetailsModal
                showModal={showModal}
                isOpen={isModalOpen}
                user={selectedUser}
            />

          </Card.Body>
        </Card>

      </React.Fragment>
  );

}

export default UserSettings;