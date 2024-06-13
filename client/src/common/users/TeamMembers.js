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
import PropTypes from "prop-types";
import {OverlayTrigger, Tooltip} from "react-bootstrap";
import {getLabelClass, getUserInitials} from "./users";

const TeamMembers = ({users, owner}) => {

  return (
      <div className={"team-members mb-3 pt-2 pb-2"}>
        {
          users
          .sort((a, b) => a.displayName.localeCompare(b.displayName))
          .sort((a, b) => owner && a.id === owner.id ? -1 : 1)
          .map((user, i) => {
            return (
                <OverlayTrigger
                    placement={"top"}
                    overlay={<Tooltip>{user.displayName}{owner && user.id === owner.id ? " (owner)" : ""}</Tooltip>}
                >
                  <div key={user.id} className={"team-member team-member-35px team-member-circle " + (owner && user.id === owner.id ? "team-member-highlight" : "")}>
                    <span className={"team-member-label fw-bold " + (getLabelClass(i))}>{getUserInitials(user)}</span>
                  </div>
                </OverlayTrigger>
            )
          })
        }
      </div>
  )
}

TeamMembers.propTypes = {
  users: PropTypes.array.isRequired,
  owner: PropTypes.object
}

export default TeamMembers;