/*
 * Copyright 2019-2024 the original author or authors.
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

import {useNavigate} from "react-router-dom";
import axios from "axios";
import {Dropdown} from "react-bootstrap";
import {Book, LogOut, Settings, User} from "react-feather";
import React from "react";
import PropTypes from "prop-types";

const NavbarUser = ({isAdmin, userId, displayName}) => {

  const navigate = useNavigate();

  const handleLogout = async () => {
    await axios.post("/logout");
    navigate(0);
  }

  return (
    <Dropdown className="nav-item" align="end">

        <span className="d-inline-block d-sm-none">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <Settings size={18} className="align-middle"/>
          </Dropdown.Toggle>
        </span>

      <span className="d-none d-sm-inline-block">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <User/>
            <span className="text-dark">
              {displayName}
            </span>
          </Dropdown.Toggle>
        </span>

      <Dropdown.Menu drop={"end"}>

        {
          !!isAdmin
            ? (
              <Dropdown.Item as={"a"} href={"/admin"}>
                <Settings size={18}
                          className="align-middle me-2"/>
                Admin Dashboard
              </Dropdown.Item>
            ) : ''
        }

        <Dropdown.Item as={"a"} href={"/user/" + userId}>
          <User size={18} className="align-middle me-2"/>
          Profile
        </Dropdown.Item>

        <Dropdown.Item as={"a"} href={"https://study-tracker.gitbook.io/documentation"}>
          <Book size={18} className="align-middle me-2"/>
          Documentation
        </Dropdown.Item>

        <Dropdown.Item
          as={"a"}
          // href={logoutUrl}
          onClick={handleLogout}
        >
          <LogOut size={18} className="align-middle me-2"/>
          Sign out
        </Dropdown.Item>

      </Dropdown.Menu>
    </Dropdown>
  )
}

NavbarUser.propTypes = {
  isAdmin: PropTypes.bool.isRequired,
  userId: PropTypes.number.isRequired,
  displayName: PropTypes.string.isRequired,
}

export default NavbarUser;