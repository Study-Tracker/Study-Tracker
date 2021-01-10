import React from "react";
import {CustomInput, FormGroup} from 'reactstrap';
import {setFilters} from "../../redux/actions/filterActions";
import {connect} from 'react-redux';
import {statuses} from "../../config/statusConstants";
import {setPrograms} from "../../redux/actions/programActions";
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import {history} from "../../App";
import {cleanQueryParams, FilterLabel, FilterSidebar} from "./filters";

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

class StudyFilters extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      defaults: defaults,
      filters: defaults,
      programs: []
    };

    this.updateFilters = this.updateFilters.bind(this);
    this.resetFilters = this.resetFilters.bind(this);
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

      const params = cleanQueryParams(
          qs.parse(this.props.location.search, {ignoreQueryPrefix: true}));
      if (params.hasOwnProperty(labels.PROGRAM)) {
        params[labels.PROGRAM] = [...params[labels.PROGRAM].split(",")];
      }
      if (params.hasOwnProperty(labels.STATUS)) {
        params[labels.STATUS] = [...params[labels.STATUS].split(",")];
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
)(StudyFilters);