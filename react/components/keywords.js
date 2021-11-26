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
import {Badge} from 'react-bootstrap'

export const KeywordCategoryBadge = ({category}) => {
  return (
      <Badge bg={"info"}>{category}</Badge>
  );
};

export const KeywordBadge = ({category, label}) => {
  return (
      <Badge bg={"info"} className="m-1">
        {category}: {label}
      </Badge>
  );
};

export const KeywordBadgeList = ({keywords}) => {
  let flag = false;
  return keywords.map(k => {
    const br = flag ? <span>&nbsp;</span> : '';
    flag = true;
    return (
        <React.Fragment key={'keyword-' + k.keyword}>
          {br}
          <KeywordBadge category={k.category} label={k.keyword}/>
        </React.Fragment>
    )
  });
};