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

import {OverlayTrigger, Tooltip} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import UserMiniAvatar from "./UserMiniAvatar";

const TeamMembersCompact = ({ users, owner, animated = false, limit = 10 }) => {
  return (
      <div
          className={`team-members ${animated ? "animated" : ""} mb-3 pt-2 pb-2`}
      >
        {users
        .sort((a, b) => a.displayName.localeCompare(b.displayName))
        .sort((a, b) => (owner && a.id === owner.id ? -1 : 1))
        .map((user, i) => {
          if (i >= limit) {
            return null;
          } else {
            return (
                <span key={user.id}>
                <OverlayTrigger
                    placement={"top"}
                    overlay={
                      <Tooltip id={`user-tooltip-${user.id}`}>
                        {user.displayName}
                        {owner && user.id === owner.id ? " (owner)" : ""}
                      </Tooltip>
                    }
                >
                  <UserMiniAvatar
                      user={user}
                      index={i}
                      highlight={owner && user.id === owner.id}
                  />
                </OverlayTrigger>
              </span>
            );
          }
        })}
        {users.length > limit && (
            <div
                className={
                  "team-member team-member-35px team-member-circle team-member-more"
                }
            >
          <span className={"team-member-label fw-bold"}>
            +{users.length - limit}
          </span>
            </div>
        )}
      </div>
  );
};

TeamMembersCompact.propTypes = {
  users: PropTypes.array.isRequired,
  owner: PropTypes.object,
  animated: PropTypes.bool,
  limit: PropTypes.number,
};

export default TeamMembersCompact;