import React from "react";
import {Form} from 'react-bootstrap';
import {FormGroup} from '../forms/common'
import {setFilters} from "../../redux/actions/filterActions";
import {connect} from 'react-redux';
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import {history} from "../../App";
import {cleanQueryParams, FilterSidebar} from "./filters";

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

class ProgramFilters extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      defaults: defaults,
      filters: defaults
    };

    this.updateFilters = this.updateFilters.bind(this);
    this.resetFilters = this.resetFilters.bind(this);
    this.props.dispatch(setFilters(defaults));
  }

  encodeFiltersAsQueryString(filters) {
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

  componentDidMount() {

    const params = cleanQueryParams(
        qs.parse(this.props.location.search, {ignoreQueryPrefix: true}));
    const filters = {
      ...this.state.defaults,
      ...params
    };
    console.log(filters);

    this.setState({
      filters: filters
    });

    this.props.dispatch(setFilters(filters));

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

            {
              !!this.props.user ? (
                  <React.Fragment>

                    <small className="d-block font-weight-bold text-muted mb-2">
                      Quick Views
                    </small>

                    <FormGroup>
                      <Form.Check
                          id="my-programs-check"
                          type="checkbox"
                          label="My Programs"
                          checked={!!this.state.filters[labels.MY_PROGRAM]}
                          onChange={(e) => this.updateFilters({
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
                    !this.state.filters[labels.ACTIVE]
                    && !this.state.filters[labels.INACTIVE]
                  }
                  onChange={() => {
                    this.updateFilters({
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
                    checked={!!this.state.filters[labels.ACTIVE]}
                    onChange={() => {
                      this.updateFilters({
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
                  checked={!!this.state.filters[labels.INACTIVE]}
                  onChange={() => {
                    this.updateFilters({
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

}

export default compose(
    withRouter,
    connect(
        store => ({
          user: store.user
        })
    )
)(ProgramFilters);