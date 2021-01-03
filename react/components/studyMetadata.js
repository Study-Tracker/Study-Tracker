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

import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserCircle} from "@fortawesome/free-solid-svg-icons";
import {KeywordCategoryBadge} from "./keywords";

export const StudyTeam = ({users, owner}) => {
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

export const StudyKeywords = ({keywords}) => {
  const list = keywords.map(keyword => {
    return (
        <li key={"keyword-" + keyword.id} className="mt-1">
          <KeywordCategoryBadge category={keyword.category}/>
          &nbsp;&nbsp;
          {keyword.keyword}
        </li>
    );
  });
  if (list.length === 0) {
    return (<p>n/a</p>);
  }
  return (
      <ul className="list-unstyled">
        {list}
      </ul>
  );
};

export const StudyCollaborator = ({collaborator, externalCode}) => {
  return (
      <div className="collaborator mb-4">
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
  );
};