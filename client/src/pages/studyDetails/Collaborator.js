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

import PropTypes from "prop-types";
import React from "react";

const Collaborator = ({collaborator, externalCode}) => {
  return (
      <div>
        <h6 className="details-label">CRO/Collaborator</h6>
        <div className="collaborator bg-light p-3 mb-4">
          <table>
            <tbody>
            <tr>
              <td><strong>External Study Code:</strong></td>
              <td><strong>{externalCode}</strong></td>
            </tr>
            <tr>
              <td><strong>Label: </strong></td>
              <td>{collaborator.label}</td>
            </tr>
            <tr>
              <td><strong>Organization Name: </strong></td>
              <td>{collaborator.organizationName}</td>
            </tr>
            <tr>
              <td><strong>Organization Location: </strong></td>
              <td>{collaborator.organizationLocation}</td>
            </tr>
            <tr>
              <td><strong>Contact Person: </strong></td>
              <td>{collaborator.contactPersonName}</td>
            </tr>
            <tr>
              <td><strong>Contact Email: </strong></td>
              <td>
                <a href={"mailto:" + collaborator.contactEmail}>
                  {collaborator.contactEmail}
                </a>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
  );
};

Collaborator.propTypes = {
  collaborator: PropTypes.object.isRequired,
  externalCode: PropTypes.string.isRequired
}

export default Collaborator;
