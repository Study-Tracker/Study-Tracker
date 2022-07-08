import React, {useEffect, useState} from "react";
import {FormGroup} from '../forms/common';
import {Form} from 'react-bootstrap';
import {FilterLabel, FilterSidebar} from "./filters";
import {setFilters} from "../../redux/filterSlice";
import {useDispatch} from "react-redux";
import {useSearchParams} from "react-router-dom";

const qs = require('qs');

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

    const filters = {
      ...state.defaults,
      ...searchParams
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
    setSearchParams(filters);
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