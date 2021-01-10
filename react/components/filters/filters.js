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

import React from "react";
import {Filter, RefreshCw} from 'react-feather';
import PerfectScrollbar from 'react-perfect-scrollbar';
import {Button} from "reactstrap";

export class FilterSidebar extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isOpen: false
    }
    this.toggleSidebar = this.toggleSidebar.bind(this);
  }

  toggleSidebar() {
    this.setState({isOpen: !this.state.isOpen});
  }

  render() {
    return (
        <div className={"settings " + (this.state.isOpen ? "open" : "")}>

          <div className="settings-toggle" onClick={() => this.toggleSidebar()}>
            <Filter size={24}/>
          </div>

          <div className="settings-panel">
            <div className="settings-content">
              <PerfectScrollbar>

                <div className="settings-title">
                  <Button close onClick={() => this.toggleSidebar()}/>
                  <h4>
                    Filters
                    &nbsp;&nbsp;
                    <a
                        className="rotate-on-hover"
                        title="Reset filters"
                        onClick={this.props.resetFilters}
                    >
                      <RefreshCw size={16}/>
                    </a>

                  </h4>
                </div>

                <hr/>

                {this.props.children}

              </PerfectScrollbar>
            </div>
          </div>

        </div>
    )
  }

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

export const FilterLabel = ({text}) => {
  return (
      <small className="d-block font-weight-bold text-muted mb-2">
        {text}
      </small>
  )
};
