import React from "react";
import {FormGroup} from '../forms/common';
import {Form} from 'react-bootstrap';
import {setFilters} from "../../redux/actions/filterActions";
import {connect} from 'react-redux';
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import {history} from "../../App";
import {cleanQueryParams, FilterLabel, FilterSidebar} from "./filters";

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

class UserFilters extends React.Component {

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

            <FilterLabel text={"Quick Views"} />

            <FormGroup>
              <Form.Check
                  id="admin-check"
                  type="checkbox"
                  label="Admin users"
                  checked={!!this.state.filters[labels.ADMIN]}
                  onChange={(e) => this.updateFilters({
                    [labels.ADMIN]: e.target.checked ? true
                        : null
                  })}
              />
            </FormGroup>

          </div>

          <hr/>

          <div className="settings-section">

            <FilterLabel text={"User Status"} />

            <FormGroup className="mb-2 ms-4">
              <Form.Check
                  label={"Show all users"}
                  type="radio"
                  name="user-status"
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
              />
            </FormGroup>

            <FormGroup className="mb-2 ms-4">
              <Form.Check
                  label={"Active users only"}
                  type="radio"
                  name="user-status"
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
                  label={"Inactive users only"}
                  type="radio"
                  name="user-status"
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
)(UserFilters);