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
import {Button, CustomInput, FormGroup} from 'reactstrap';
import {setFilters} from "../redux/actions/filterActions";
import {connect} from 'react-redux';
import {statuses} from "../config/statusConstants";
import {setPrograms} from "../redux/actions/programActions";
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import {history} from "../App";

const qs = require('qs');

export const labels = {
  LEGACY: "legacy",
  MY_STUDY: "myStudy",
  PROGRAM: "program",
  EXTERNAL: "external",
  STATUS: "status",
  ACTIVE: "active"
};

const defaults = {
  [labels.LEGACY]: null,
  [labels.MY_STUDY]: null,
  [labels.EXTERNAL]: null,
  [labels.ACTIVE]: null,
  [labels.STATUS]: Object.values(statuses).map(status => status.value),
  [labels.PROGRAM]: []
};

class Filters extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      isOpen: false,
      defaults: defaults,
      filters: defaults,
      programs: []
    };

    this.toggleSidebar = this.toggleSidebar.bind(this);
    this.updateFilters = this.updateFilters.bind(this);
    this.resetFilters = this.resetFilters.bind(this);
    this.props.dispatch(setFilters(defaults));
  }

  cleanQueryParams(params) {
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

  encodeFiltersAsQueryString(filters) {
    const keys = Object.keys(filters);
    let params = [];
    for (const k of keys) {
      if (k === labels.PROGRAM) {
        if (filters[k].length < this.state.programs.length) {
          const param = k + "=" + filters[k].join(",");
          params.push(param);
        }
      } else if (k === labels.STATUS) {
        if (filters[k].length < Object.keys(statuses).length) {
          const param = k + "=" + filters[k].join(",");
          params.push(param);
        }
      } else {
        if (Array.isArray(filters[k])) {
          const param = k + "=" + filters[k].join(",");
          params.push(param);
        } else {
          if (!!filters[k]) {
            const param = k + "=" + filters[k];
            params.push(param);
          }
        }
      }
    }
    return params.join("&");
  }

  componentDidMount() {
    fetch("/api/program")
    .then(response => response.json())
    .then(programs => {

      programs = programs.sort((a, b) => {
        if (a.name < b.name) {
          return -1;
        } else if (a.name > b.name) {
          return 1;
        } else {
          return 0;
        }
      });

      const params = this.cleanQueryParams(
          qs.parse(this.props.location.search, {ignoreQueryPrefix: true}));
      if (params.hasOwnProperty(
          labels.PROGRAM)) {
        params[labels.PROGRAM] = [...params[labels.PROGRAM].split(
            ",")];
      }
      if (params.hasOwnProperty(
          labels.STATUS)) {
        params[labels.STATUS] = [...params[labels.STATUS].split(
            ",")];
      }

      const updatedDefaults = {
        ...this.state.defaults,
        [labels.PROGRAM]: programs.map(p => p.id)
      };

      const filters = {
        ...updatedDefaults,
        ...params
      };
      console.log(filters);

      this.setState({
        programs: programs,
        defaults: updatedDefaults,
        filters: filters
      });

      this.props.dispatch(setPrograms(programs));
      this.props.dispatch(setFilters(filters))

    }).catch(e => {
      console.error(e);
    })
  }

  toggleSidebar() {
    this.setState({isOpen: !this.state.isOpen});
  }

  updateFilters(filter) {
    const filters = {
      ...this.state.filters,
      ...filter
    };
    this.props.dispatch(setFilters(filters));
    this.setState({
      filters
    });
    history.push({
      search: this.encodeFiltersAsQueryString(filters)
    });
  }

  resetFilters() {
    this.updateFilters(this.state.defaults);
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
                        onClick={this.resetFilters}
                    >
                      <RefreshCw size={16}/>
                    </a>

                  </h4>
                </div>

                <hr/>

                <div className="settings-section">

                  <small className="d-block font-weight-bold text-muted mb-2">
                    Quick Views
                  </small>

                  {
                    !!this.props.user ? (
                        <FormGroup>
                          <CustomInput
                              id="my-studies-check"
                              type="checkbox"
                              label="My Studies"
                              checked={!!this.state.filters[labels.MY_STUDY]}
                              onChange={(e) => this.updateFilters({
                                [labels.MY_STUDY]: e.target.checked ? true
                                    : null
                              })}
                          />
                        </FormGroup>
                    ) : ''
                  }

                  <FormGroup>
                    <CustomInput
                        id="legacy-study-check"
                        type="checkbox"
                        label="Legacy Studies"
                        checked={!!this.state.filters[labels.LEGACY]}
                        onChange={(e) => this.updateFilters({
                          [labels.LEGACY]: e.target.checked ? true : null
                        })}
                    />
                  </FormGroup>

                  <FormGroup>
                    <CustomInput
                        id="cro-study-check"
                        type="checkbox"
                        label="External Studies"
                        checked={!!this.state.filters[labels.EXTERNAL]}
                        onChange={(e) => this.updateFilters({
                          [labels.EXTERNAL]: e.target.checked ? true : null
                        })}
                    />
                  </FormGroup>

                </div>

                <hr/>

                <div className="settings-section">

                  <FormGroup>
                    <FilterLabel text="Status"/>
                    <div>
                      {
                        Object.values(statuses).map(status => {
                          return (
                              <CustomInput
                                  key={"status-checkbox-" + status.value}
                                  id={"status-checkbox-" + status.value}
                                  type={"checkbox"}
                                  label={status.label}
                                  checked={this.state.filters[labels.STATUS].indexOf(
                                      status.value) > -1}
                                  onChange={(e) => {
                                    let values = this.state.filters[labels.STATUS];
                                    if (e.target.checked) {
                                      values.push(
                                          status.value);
                                    } else {
                                      values = values.filter(
                                          v => v !== status.value);
                                    }
                                    this.updateFilters({
                                      [labels.STATUS]: values
                                    })
                                  }}
                              />
                          )
                        })
                      }

                    </div>
                  </FormGroup>

                  {
                    !!this.state.programs ? (
                        <FormGroup>
                          <FilterLabel text={"Programs"}/>
                          <div>
                            {
                              this.state.programs.map(program => {
                                return (
                                    <CustomInput
                                        key={"program-checkbox-" + program.id}
                                        id={"program-checkbox-" + program.id}
                                        type={"checkbox"}
                                        label={program.name}
                                        checked={this.state.filters[labels.PROGRAM].indexOf(
                                            program.id) > -1}
                                        onChange={(e) => {
                                          let values = this.state.filters[labels.PROGRAM];
                                          if (e.target.checked) {
                                            values.push(program.id);
                                          } else {
                                            values = values.filter(
                                                v => v !== program.id);
                                          }
                                          this.updateFilters({
                                            [labels.PROGRAM]: values
                                          })
                                        }}
                                    />
                                )
                              })
                            }

                          </div>
                        </FormGroup>
                    ) : ''
                  }

                </div>

              </PerfectScrollbar>
            </div>
          </div>

        </div>
    )

  }

}

const FilterLabel = ({text}) => {
  return (
      <small className="d-block font-weight-bold text-muted mb-2">
        {text}
      </small>
  )
};

export default compose(
    withRouter,
    connect(
        store => ({
          user: store.user
        })
    )
)(Filters);