import React from "react";
import {CustomInput, FormGroup, Input, Label} from 'reactstrap';
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

            <small className="d-block font-weight-bold text-muted mb-2">
              Quick Views
            </small>

            <FormGroup>
              <CustomInput
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

            <small className="d-block font-weight-bold text-muted mb-2">
              User Status
            </small>

            <FormGroup className="mb-2 ml-4">
              <Label check>
                <Input
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
                {" "}
                Show all users
              </Label>
            </FormGroup>

            <FormGroup className="mb-2 ml-4">
              <Label check>
                <Input
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
                {" "}
                Active users only
              </Label>
            </FormGroup>

            <FormGroup className="mb-2 ml-4">
              <Label check>
                <Input
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
                {" "}
                Inactive users only
              </Label>
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