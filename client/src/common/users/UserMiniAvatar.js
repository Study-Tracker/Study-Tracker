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

import React from "react";
import {getLabelClass, getUserInitials} from "./users";
import PropTypes from "prop-types";

const UserMiniAvatar = React.forwardRef(
    ({ user, highlight = false, index = 0, ...props }, ref) => {
      return (
        <div
            ref={ref}
            className={
                "me-1 team-member team-member-35px team-member-circle " +
                (highlight ? "team-member-highlight" : "")
            }
            {...props}
        >
          <span className={"team-member-label fw-bold " + getLabelClass(index)}>
            {getUserInitials(user)}
          </span>
        </div>
      );
    }
);

UserMiniAvatar.propTypes = {
  user: PropTypes.object.isRequired,
  highlight: PropTypes.bool,
  index: PropTypes.number,
  icon: PropTypes.any,
  image: PropTypes.object,
};

export default UserMiniAvatar;