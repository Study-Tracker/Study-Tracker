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

import React, {useEffect, useState} from "react";
import {Form} from 'react-bootstrap';
import {FormGroup} from '../forms/common';
import {statuses} from "../../config/statusConstants";
import {
  convertSearchParams,
  FilterLabel,
  filterNullSearchParams,
  FilterSidebar
} from "./filters";
import {useDispatch, useSelector} from "react-redux";
import {useSearchParams} from "react-router-dom";
import {setFilters} from "../../redux/filterSlice";
import {setPrograms} from "../../redux/programSlice";
import axios from "axios";

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
  [labels.ACTIVE]: true,
  [labels.STATUS]: Object.values(statuses).map(status => status.value),
  [labels.PROGRAM]: []
};

const StudyFilters = () => {

  const dispatch = useDispatch();
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    defaults: defaults,
    filters: defaults,
    programs: []
  });
  const user = useSelector(s => s.user.value);

  useEffect(() => {
    dispatch(setFilters(defaults));
    axios.get("/api/internal/program")
    .then(response => {

      const programs = response.data.sort((a, b) => {
        if (a.name < b.name) {
          return -1;
        } else if (a.name > b.name) {
          return 1;
        } else {
          return 0;
        }
      });

      const params = convertSearchParams(searchParams);
      console.debug("Params", params);
      if (params.hasOwnProperty(labels.PROGRAM)) {
        const p = params[labels.PROGRAM]
        .map(p => parseInt(p, 10));
        params[labels.PROGRAM] = [...p];
      }

      const updatedDefaults = {
        ...state.defaults,
        [labels.PROGRAM]: programs.map(p => p.id)
      };

      const filters = {
        ...updatedDefaults,
        ...params
      };

      setState(prevState => ({
          ...prevState,
          programs: programs,
          defaults: updatedDefaults,
          filters: filters
      }));

      dispatch(setPrograms(programs));
      dispatch(setFilters(filters))

    }).catch(e => {
      console.error(e);
    })
  }, [dispatch]);

  const updateFilters = (filter) => {
    console.debug("New filter", filter);
    const filters = {
      ...state.filters,
      ...filter
    };
    console.debug("Update filters", filters);
    dispatch(setFilters(filters));
    setState(prevState => ({
      ...prevState,
      filters
    }));
    setSearchParams(filterNullSearchParams(filters));
  }

  const toggleAllStatusFilters = () => {
    let filter = state.filters[labels.STATUS];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(statuses).map(status => status.value);
    }
    updateFilters({[labels.STATUS]: filter})
  };

  const toggleAllProgramFilters = () => {
    let filter = state.filters[labels.PROGRAM];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(state.programs)
      .map(program => program.id);
    }
    updateFilters({[labels.PROGRAM]: filter})
  }

  const resetFilters = () => {
    updateFilters(state.defaults);
  }

  return (
      <FilterSidebar resetFilters={resetFilters}>

        <div className="settings-section">

          <FilterLabel text={"Quick Views"}/>

          {
            user && (
                <FormGroup>
                  <Form.Check
                      id="my-studies-check"
                      type="checkbox"
                      label="My Studies"
                      checked={!!state.filters[labels.MY_STUDY]}
                      onChange={(e) => updateFilters({
                        [labels.MY_STUDY]: e.target.checked ? true
                            : null
                      })}
                  />
                </FormGroup>
            )
          }

          <FormGroup>
            <Form.Check
                id="legacy-study-check"
                type="checkbox"
                label="Legacy Studies"
                checked={!!state.filters[labels.LEGACY]}
                onChange={(e) => updateFilters({
                  [labels.LEGACY]: e.target.checked ? true : null
                })}
            />
          </FormGroup>

          <FormGroup>
            <Form.Check
                id="cro-study-check"
                type="checkbox"
                label="External Studies"
                checked={!!state.filters[labels.EXTERNAL]}
                onChange={(e) => updateFilters({
                  [labels.EXTERNAL]: e.target.checked ? true : null
                })}
            />
          </FormGroup>

          <FormGroup>
            <Form.Check
                id="inactive-study-check"
                type="checkbox"
                label="Removed Studies"
                checked={state.filters[labels.ACTIVE] === false}
                onChange={(e) => updateFilters({
                  [labels.ACTIVE]: !e.target.checked
                })}
            />
          </FormGroup>

        </div>

        <hr/>

        <div className="settings-section">

          <FormGroup>
            <FilterLabel text="Status" toggle={toggleAllStatusFilters}/>
            <div>
              {
                Object.values(statuses).map(status => {
                  return (
                      <Form.Check
                          key={"status-checkbox-" + status.value}
                          id={"status-checkbox-" + status.value}
                          type={"checkbox"}
                          label={status.label}
                          checked={state.filters[labels.STATUS].indexOf(
                              status.value) > -1}
                          onChange={(e) => {
                            let values = [...state.filters[labels.STATUS]];
                            if (e.target.checked) {
                              values.push(status.value);
                            } else {
                              values = values.filter(
                                  v => v !== status.value);
                            }
                            updateFilters({
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
            !!state.programs ? (
                <FormGroup>
                  <FilterLabel text={"Programs"}
                               toggle={toggleAllProgramFilters}/>
                  <div>
                    {
                      state.programs.map(program => {
                        return (
                            <Form.Check
                                key={"program-checkbox-" + program.id}
                                id={"program-checkbox-" + program.id}
                                type={"checkbox"}
                                label={program.name}
                                checked={state.filters[labels.PROGRAM].indexOf(
                                    program.id) > -1}
                                onChange={(e) => {
                                  let values = [...state.filters[labels.PROGRAM]];
                                  if (e.target.checked) {
                                    values.push(program.id);
                                  } else {
                                    values = values.filter(
                                        v => v !== program.id);
                                  }
                                  updateFilters({
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

      </FilterSidebar>
  )

}

export default StudyFilters;