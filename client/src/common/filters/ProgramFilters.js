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
import {setFilters} from "../../redux/filterSlice";
import {
  convertSearchParams,
  filterNullSearchParams,
  FilterSidebar
} from "./filters";
import {useDispatch, useSelector} from "react-redux";
import {useSearchParams} from "react-router-dom";

export const labels = {
  ACTIVE: "active",
  INACTIVE: "inactive",
  MY_PROGRAM: "myProgram"
};

const defaults = {
  [labels.ACTIVE]: null,
  [labels.INACTIVE]: null,
  [labels.MY_PROGRAM]: null
};

const ProgramFilters = props => {

  const dispatch = useDispatch();
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    defaults: defaults,
    filters: defaults
  });
  const user = useSelector(s => s.user.value);

  useEffect(() => {

    dispatch(setFilters(defaults));
    const params = convertSearchParams(searchParams);
    
    const filters = {
      ...state.defaults,
      ...params
    };
    console.debug(filters);

    setState(prevState => ({
      ...prevState,
      filters: filters
    }));

    dispatch(setFilters(filters));

  }, []);

  const updateFilters = (filter) => {
    const filters = {
      ...state.filters,
      ...filter
    };
    dispatch(setFilters(filters));
    setState(prevState => ({
      ...prevState,
      filters
    }));
    setSearchParams(filterNullSearchParams(filters));
  };

  const resetFilters = () => {
    updateFilters(state.defaults);
  }

  return (
      <FilterSidebar resetFilters={resetFilters}>

        <div className="settings-section">

          {
            !!user ? (
                <React.Fragment>

                  <small className="d-block font-weight-bold text-muted mb-2">
                    Quick Views
                  </small>

                  <FormGroup>
                    <Form.Check
                        id="my-programs-check"
                        type="checkbox"
                        label="My Programs"
                        checked={!!state.filters[labels.MY_PROGRAM]}
                        onChange={(e) => updateFilters({
                          [labels.MY_PROGRAM]: e.target.checked ? true : null
                        })}
                    />
                  </FormGroup>

                </React.Fragment>
            ) : ''
          }

          <small className="d-block font-weight-bold text-muted mb-2">
            Program Status
          </small>

          <FormGroup className="mb-2 ms-4">
            <Form.Check
                type="radio"
                name="program-status"
                checked={
                    !state.filters[labels.ACTIVE]
                    && !state.filters[labels.INACTIVE]
                }
                onChange={() => {
                  updateFilters({
                    [labels.ACTIVE]: null,
                    [labels.INACTIVE]: null
                  })
                }}
                label={"Show all programs"}
            />
          </FormGroup>

          <FormGroup className="mb-2 ms-4">

            <Form.Check
                label={"Active programs only"}
                type="radio"
                name="program-status"
                checked={!!state.filters[labels.ACTIVE]}
                onChange={() => {
                  updateFilters({
                    [labels.ACTIVE]: true,
                    [labels.INACTIVE]: null
                  })
                }}
            />
          </FormGroup>

          <FormGroup className="mb-2 ms-4">
            <Form.Check
                label={"Inactive programs only"}
                type="radio"
                name="program-status"
                checked={!!state.filters[labels.INACTIVE]}
                onChange={() => {
                  updateFilters({
                    [labels.ACTIVE]: null,
                    [labels.INACTIVE]: true
                  })
                }}
            />
          </FormGroup>

        </div>

      </FilterSidebar>
  )

}

export default ProgramFilters;