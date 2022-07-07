import React, {useEffect, useState} from "react";
import {Form} from 'react-bootstrap';
import {FormGroup} from '../forms/common';
import {setFilters} from "../../redux/filterSlice";
import {cleanQueryParams, FilterSidebar} from "./filters";
import {useDispatch, useSelector} from "react-redux";
import {useLocation, useSearchParams} from "react-router-dom";

const qs = require('qs');

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
  const location = useLocation();

  // dispatch(setFilters(defaults));

  const encodeFiltersAsQueryString = (filters) => {
    const keys = Object.keys(filters);
    let params = [];
    for (const k of keys) {
      if (!!filters[k]) {
        const param = k + "=" + filters[k];
        params.push(param);
      }
    }
    return params.join("&");
  }

  useEffect(() => {

    dispatch(setFilters(defaults));
    
    const params = cleanQueryParams(
        qs.parse(location.search, {ignoreQueryPrefix: true}));
    const filters = {
      ...state.defaults,
      ...params
    };
    console.debug(filters);

    setState({
      ...state,
      filters: filters
    });

    dispatch(setFilters(filters));

  }, []);

  const updateFilters = (filter) => {
    const filters = {
      ...state.filters,
      ...filter
    };
    dispatch(setFilters(filters));
    setState({
      ...state,
      filters
    });
    setSearchParams(filters);
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