import React, {useEffect, useState} from "react";
import {FormGroup} from '../forms/common';
import {Form} from 'react-bootstrap';
import {statuses} from "../../config/statusConstants";
import {cleanQueryParams, FilterLabel, FilterSidebar} from "./filters";
import {setPrograms} from "../../redux/programSlice";
import {setFilters} from "../../redux/filterSlice";
import {setAssayTypes} from "../../redux/assayTypeSlice";
import {useDispatch} from "react-redux";
import {useSearchParams} from "react-router-dom";

const qs = require("qs");

export const labels = {
  LEGACY: "legacy",
  MY_ASSAY: "myAssay",
  PROGRAM: "program",
  EXTERNAL: "external",
  STATUS: "status",
  ACTIVE: "active",
  ASSAY_TYPE: "assayType"
};

const defaults = {
  [labels.LEGACY]: null,
  [labels.MY_ASSAY]: null,
  [labels.EXTERNAL]: null,
  [labels.ACTIVE]: null,
  [labels.STATUS]: Object.values(statuses).map(status => status.value),
  [labels.PROGRAM]: [],
  [labels.ASSAY_TYPE]: []
};

const AssayFilters = props => {

  const dispatch = useDispatch();
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    defaults: defaults,
    filters: defaults,
    programs: [],
    assayTypes: []
  });
  
  //dispatch(setFilters(defaults));

  const encodeFiltersAsQueryString = (filters) => {
    const keys = Object.keys(filters);
    let params = [];
    for (const k of keys) {
      if (k === labels.PROGRAM) {
        if (filters[k].length < state.programs.length) {
          const param = k + "=" + filters[k].join(",");
          params.push(param);
        }
      } else if (k === labels.ASSAY_TYPE) {
        if (filters[k].length < state.assayTypes.length) {
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

  const toggleAllStatusFilters = () => {
    let filter = state.filters[labels.STATUS];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(statuses).map(status => status.value);
    }
    updateFilters({[labels.STATUS]: filter})
  }

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

  const toggleAllAssayTypeFilters = () => {
    let filter = state.filters[labels.ASSAY_TYPE];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(state.assayTypes)
      .map(type => type.id);
    }
    updateFilters({[labels.ASSAY_TYPE]: filter})
  }

  useEffect(() => {

    dispatch(setFilters(defaults));

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

      fetch("/api/assaytype")
      .then(response => response.json())
      .then(assayTypes => {

        assayTypes = assayTypes.sort((a, b) => {
          if (a.name < b.name) {
            return -1;
          } else if (a.name > b.name) {
            return 1;
          } else {
            return 0;
          }
        });

        const params = cleanQueryParams(
            qs.parse(props.location.search, {ignoreQueryPrefix: true}));
        if (params.hasOwnProperty(labels.PROGRAM)) {
          const p = String(params[labels.PROGRAM])
          .split(",")
          .map(p => parseInt(p));
          params[labels.PROGRAM] = [...p];
        }
        if (params.hasOwnProperty(labels.ASSAY_TYPE)) {
          const t = String(params[labels.ASSAY_TYPE])
          .split(",")
          .map(p => parseInt(p));
          params[labels.ASSAY_TYPE] = [...t];
        }
        if (params.hasOwnProperty(labels.STATUS)) {
          params[labels.STATUS] = [...params[labels.STATUS].split(",")];
        }

        const updatedDefaults = {
          ...state.defaults,
          [labels.PROGRAM]: programs.map(p => p.id),
          [labels.ASSAY_TYPE]: assayTypes.map(t => t.id)
        };

        const filters = {
          ...updatedDefaults,
          ...params
        };
        console.log(filters);

        setState({ ...state,
          programs: programs,
          assayTypes: assayTypes,
          defaults: updatedDefaults,
          filters: filters
        });

        dispatch(setPrograms(programs));
        dispatch(setAssayTypes(assayTypes));
        dispatch(setFilters(filters))

      })

    }).catch(e => {
      console.error(e);
    })
  }, []);

  const updateFilters = (filter) => {
    const filters = {
      ...state.filters,
      ...filter
    };
    dispatch(setFilters(filters));
    setState({ ...state,
      filters
    });
    setSearchParams(filters);
  };

  const resetFilters = () => {
    updateFilters(state.defaults);
  };

  return (
      <FilterSidebar resetFilters={resetFilters}>

        <div className="settings-section">

          <FilterLabel text={"Quick Views"}/>

          {
            !!props.user ? (
                <FormGroup>
                  <Form.Check
                      id="my-assays-check"
                      type="checkbox"
                      label="My Assays"
                      checked={!!state.filters[labels.MY_ASSAY]}
                      onChange={(e) => updateFilters({
                        [labels.MY_ASSAY]: e.target.checked ? true
                            : null
                      })}
                  />
                </FormGroup>
            ) : ''
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
                            let values = state.filters[labels.STATUS];
                            if (e.target.checked) {
                              values.push(
                                  status.value);
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
                                  let values = state.filters[labels.PROGRAM];
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

          {
            !!state.assayTypes ? (
                <FormGroup>
                  <FilterLabel text={"Assay Types"}
                               toggle={toggleAllAssayTypeFilters}/>
                  <div>
                    {
                      state.assayTypes.map(assayType => {
                        return (
                            <Form.Check
                                key={"assay-type-checkbox-" + assayType.id}
                                id={"assay-type-checkbox-" + assayType.id}
                                type={"checkbox"}
                                label={assayType.name}
                                checked={state.filters[labels.ASSAY_TYPE].indexOf(
                                    assayType.id) > -1}
                                onChange={(e) => {
                                  let values = state.filters[labels.ASSAY_TYPE];
                                  if (e.target.checked) {
                                    values.push(assayType.id);
                                  } else {
                                    values = values.filter(
                                        v => v !== assayType.id);
                                  }
                                  updateFilters({
                                    [labels.ASSAY_TYPE]: values
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

export default  AssayFilters;