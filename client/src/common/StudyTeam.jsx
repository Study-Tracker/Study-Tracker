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
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserCircle} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";

const StudyTeam = ({users, owner}) => {
  const list = users.map(user => {
    if (user.id === owner.id) {
      return (
          <li key={new Date()}>
            <a href={"mailto:" + user.email} className={"text-info"}>
              <FontAwesomeIcon icon={faUserCircle}/>
              &nbsp;
              {user.displayName} (owner)
            </a>
          </li>);
    } else {
      return (
          <li key={"study-user-" + user.id}>
            <FontAwesomeIcon icon={faUserCircle}/>
            &nbsp;
            {user.displayName}
          </li>
      );
    }
  });
  return (
      <ul className="list-unstyled">
        {list}
      </ul>
  );
};

StudyTeam.propTypes = {
  users: PropTypes.array.isRequired,
  owner: PropTypes.object.isRequired
}

export default StudyTeam;
