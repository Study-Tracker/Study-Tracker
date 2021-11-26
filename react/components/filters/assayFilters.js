import React from "react";
import {FormGroup} from '../forms/common';
import {Form} from 'react-bootstrap';
import {setFilters} from "../../redux/actions/filterActions";
import {connect} from 'react-redux';
import {statuses} from "../../config/statusConstants";
import {setPrograms} from "../../redux/actions/programActions";
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import {history} from "../../App";
import {cleanQueryParams, FilterLabel, FilterSidebar} from "./filters";
import {setAssayTypes} from "../../redux/actions/assayTypeActions";

const qs = require('qs');

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

class AssayFilters extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      defaults: defaults,
      filters: defaults,
      programs: [],
      assayTypes: []
    };

    this.updateFilters = this.updateFilters.bind(this);
    this.resetFilters = this.resetFilters.bind(this);
    this.toggleAllProgramFilters = this.toggleAllProgramFilters.bind(this);
    this.toggleAllStatusFilters = this.toggleAllStatusFilters.bind(this);
    this.toggleAllAssayTypeFilters = this.toggleAllAssayTypeFilters.bind(this);
    this.props.dispatch(setFilters(defaults));
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
      } else if (k === labels.ASSAY_TYPE) {
        if (filters[k].length < this.state.assayTypes.length) {
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

  toggleAllStatusFilters() {
    let filter = this.state.filters[labels.STATUS];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(statuses).map(status => status.value);
    }
    this.updateFilters({[labels.STATUS]: filter})
  }

  toggleAllProgramFilters() {
    let filter = this.state.filters[labels.PROGRAM];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(this.state.programs)
      .map(program => program.id);
    }
    this.updateFilters({[labels.PROGRAM]: filter})
  }

  toggleAllAssayTypeFilters() {
    let filter = this.state.filters[labels.ASSAY_TYPE];
    if (!!filter && filter.length > 0) {
      filter = [];
    } else {
      filter = Object.values(this.state.assayTypes)
      .map(type => type.id);
    }
    this.updateFilters({[labels.ASSAY_TYPE]: filter})
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
            qs.parse(this.props.location.search, {ignoreQueryPrefix: true}));
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
          ...this.state.defaults,
          [labels.PROGRAM]: programs.map(p => p.id),
          [labels.ASSAY_TYPE]: assayTypes.map(t => t.id)
        };

        const filters = {
          ...updatedDefaults,
          ...params
        };
        console.log(filters);

        this.setState({
          programs: programs,
          assayTypes: assayTypes,
          defaults: updatedDefaults,
          filters: filters
        });

        this.props.dispatch(setPrograms(programs));
        this.props.dispatch(setAssayTypes(assayTypes));
        this.props.dispatch(setFilters(filters))

      })

    }).catch(e => {
      console.error(e);
    })
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
        <FilterSidebar resetFilters={this.resetFilters}>

          <div className="settings-section">

            <FilterLabel text={"Quick Views"} />

            {
              !!this.props.user ? (
                  <FormGroup>
                    <Form.Check
                        id="my-assays-check"
                        type="checkbox"
                        label="My Assays"
                        checked={!!this.state.filters[labels.MY_ASSAY]}
                        onChange={(e) => this.updateFilters({
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
                  checked={!!this.state.filters[labels.LEGACY]}
                  onChange={(e) => this.updateFilters({
                    [labels.LEGACY]: e.target.checked ? true : null
                  })}
              />
            </FormGroup>

            <FormGroup>
              <Form.Check
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
              <FilterLabel text="Status" toggle={this.toggleAllStatusFilters}/>
              <div>
                {
                  Object.values(statuses).map(status => {
                    return (
                        <Form.Check
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
                    <FilterLabel text={"Programs"} toggle={this.toggleAllProgramFilters}/>
                    <div>
                      {
                        this.state.programs.map(program => {
                          return (
                              <Form.Check
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

            {
              !!this.state.assayTypes ? (
                  <FormGroup>
                    <FilterLabel text={"Assay Types"} toggle={this.toggleAllAssayTypeFilters}/>
                    <div>
                      {
                        this.state.assayTypes.map(assayType => {
                          return (
                              <Form.Check
                                  key={"assay-type-checkbox-" + assayType.id}
                                  id={"assay-type-checkbox-" + assayType.id}
                                  type={"checkbox"}
                                  label={assayType.name}
                                  checked={this.state.filters[labels.ASSAY_TYPE].indexOf(
                                      assayType.id) > -1}
                                  onChange={(e) => {
                                    let values = this.state.filters[labels.ASSAY_TYPE];
                                    if (e.target.checked) {
                                      values.push(assayType.id);
                                    } else {
                                      values = values.filter(
                                          v => v !== assayType.id);
                                    }
                                    this.updateFilters({
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

}

export default compose(
    withRouter,
    connect(
        store => ({
          user: store.user
        })
    )
)(AssayFilters);