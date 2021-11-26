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

import React, {useState} from "react";
import {Filter, RefreshCw} from 'react-feather';
import PerfectScrollbar from 'react-perfect-scrollbar';
import {Button} from "react-bootstrap";
import useOuterClick from "../../hooks/useOuterClick";

export const FilterSidebar = (props) => {

  const [isOpen, setIsOpen] = useState(false);

  const innerRef = useOuterClick(() => {
    setIsOpen(false);
  });

  return (
      <div
          ref={innerRef}
          className={"settings js-settings " + (isOpen ? "open" : "")}
      >

        <div className="settings-toggle">
          <div className="settings-toggle-option settings-toggle-option-text js-settings-toggle" onClick={() => setIsOpen(true)}>
            <Filter size={24} className="feather align-middle" />
            &nbsp;
            Filters
          </div>
        </div>

        <div className="settings-panel">
          <div className="settings-content">
            <PerfectScrollbar>

              <div className="settings-title d-flex align-items-center">
                <button
                    type="button"
                    className="btn-close float-end js-settings-toggle"
                    aria-label="Close"
                    onClick={() => setIsOpen(false)}
                ></button>
                <h4 className="mb-0 ms-2 d-inline-block">
                  Filters
                  &nbsp;&nbsp;
                  <a
                      title="Reset filters"
                      onClick={props.resetFilters}
                  >
                    <RefreshCw className="rotate-on-hover" size={16}/>
                  </a>
                </h4>
              </div>

              {props.children}

            </PerfectScrollbar>
          </div>
        </div>

      </div>
  );

}

export const cleanQueryParams = (params) => {
  const keys = Object.keys(params);
  for (const k of keys) {
    if (params[k] === 'true') {
      params[k] = true;
    } else if (params[k] === 'false') {
      params[k] = false;
    } else if (!isNaN(params[k])) {
      params[k] = parseInt(params[k]);
    }
  }
  return params;
}

export const FilterLabel = ({text, toggle}) => {
  return (
      <strong className="d-block font-weight-bold text-muted mb-2">
        {text}
        {
          !!toggle ? (
              <span className="float-end">
                <Button size="sm" variant="link" onClick={toggle}>Toggle All</Button>
              </span>
          ) : ''
        }
      </strong>
  )
};
