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
import {Breadcrumb} from "react-bootstrap";
import PropTypes from "prop-types";

export const Breadcrumbs = ({crumbs}) => {
  const steps = crumbs.map(c => (
      <Breadcrumb.Item key={"nav-breadcrumb-" + c.label} {...(!!c.url
          ? {href: c.url} : {active: true})}>
        {c.label}
      </Breadcrumb.Item>
  ))
  return (
      <Breadcrumb>
        {steps}
      </Breadcrumb>
  )
}

Breadcrumbs.propTypes = {
  crumbs: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    url: PropTypes.string
  })).isRequired
}