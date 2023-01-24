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
import {FormGroup} from '../forms/common';
import {Form} from 'react-bootstrap';
import {
  convertSearchParams,
  FilterLabel,
  filterNullSearchParams,
  FilterSidebar
} from "./filters";
import {setFilters} from "../../redux/filterSlice";
import {useDispatch} from "react-redux";
import {useSearchParams} from "react-router-dom";

export const labels = {
  ACTIVE: "active",
  INACTIVE: "inactive",
  ADMIN: "admin"
};

const defaults = {
  [labels.ACTIVE]: null,
  [labels.INACTIVE]: null,
  [labels.ADMIN]: null
};

const UserFilters = props => {

  const [searchParams, setSearchParams] = useSearchParams();
  const dispatch = useDispatch();
  const [state, setState] = useState({
    defaults: defaults,
    filters: defaults
  });

  useEffect(() => {

    dispatch(setFilters(defaults));
    const params = convertSearchParams(searchParams);

    const filters = {
      ...state.defaults,
      ...params
    };

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
  }

  const resetFilters = () => {
    updateFilters(state.defaults);
  };

  return (
      <FilterSidebar resetFilters={resetFilters}>

        <div className="settings-section">

          <FilterLabel text={"Quick Views"}/>

          <FormGroup>
            <Form.Check
                id="admin-check"
                type="checkbox"
                label="Admin users"
                checked={!!state.filters[labels.ADMIN]}
                onChange={(e) => updateFilters({
                  [labels.ADMIN]: e.target.checked ? true
                      : null
                })}
            />
          </FormGroup>

        </div>

        <hr/>

        <div className="settings-section">

          <FilterLabel text={"User Status"}/>

          <FormGroup className="mb-2 ms-4">
            <Form.Check
                label={"Show all users"}
                type="radio"
                name="user-status"
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
            />
          </FormGroup>

          <FormGroup className="mb-2 ms-4">
            <Form.Check
                label={"Active users only"}
                type="radio"
                name="user-status"
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
                label={"Inactive users only"}
                type="radio"
                name="user-status"
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

export default UserFilters;