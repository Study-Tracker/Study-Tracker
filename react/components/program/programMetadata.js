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
import {KeywordTypeBadge} from "../keywords";

export const ProgramTeam = ({program, studies}) => {
  let users = [];
  users.push(program.createdBy.displayName);
  for (let study of studies) {
    for (let user of study.users) {
      if (users.indexOf(user.displayName) === -1) {
        users.push(user.displayName);
      }
    }
  }
  const list = users.map(user => {
    return (
        <li key={"program-user-" + user}>
          <FontAwesomeIcon icon={faUserCircle}/>
          &nbsp;
          {user}
        </li>
    );
  });
  return (
      <ul className="list-unstyled">
        {list}
      </ul>
  );
};

export const ProgramKeywords = ({studies}) => {
  let keywords = {};
  for (let study of studies) {
    for (let kw of study.keywords) {
      let key = kw.type + "::" + kw.keyword;
      if (!keywords.hasOwnProperty(key)) {
        keywords[key] = kw;
      }
    }
  }
  const list = Object.values(keywords).map(keyword => {
    return (
        <li key={"keyword-" + keyword.id} className="mt-1">
          <KeywordTypeBadge type={keyword.type}/>
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
