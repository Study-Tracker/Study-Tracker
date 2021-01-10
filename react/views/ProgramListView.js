/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import {connect} from 'react-redux';
import {compose} from 'redux';
import crossfilter from "crossfilter2";
import {withRouter} from 'react-router-dom';
import ProgramList from "../components/program/ProgramList";
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import ProgramFilters, {labels as filter} from "../components/filters/programFilters";

const qs = require('qs');

class ProgramListView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false,
      title: this.props.title,
      data: {}
    };
    this.indexPrograms = this.indexPrograms.bind(this);
    this.applyFilters = this.applyFilters.bind(this);
  }

  indexPrograms(programs) {
    console.log(programs);
    const data = {};
    data.cf = crossfilter(programs);
    data.dimensions = {};
    data.dimensions.allData = data.cf.dimension(d => d);
    data.dimensions[filter.ACTIVE] = data.cf.dimension(d => d.active)
    data.dimensions[filter.INACTIVE] = data.cf.dimension(d => !d.active)
    // data.dimensions[filter.MY_PROGRAM] = data.cf.dimension(
    //     d => this.props.user && d.owner.id === this.props.user.id);

    this.setState({
      data: data,
      isLoaded: true
    });
  }

  applyFilters(filters) {
    for (let key of Object.keys(this.state.data.dimensions)) {
      this.state.data.dimensions[key].filterAll();
      if (filters.hasOwnProperty(key) && filters[key] != null) {
        if (Array.isArray(filters[key])) {
          this.state.data.dimensions[key].filter(
              d => filters[key].indexOf(d) > -1);
        } else {
          this.state.data.dimensions[key].filter(filters[key]);
        }
      }
    }
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    let title = params.title || this.state.title;
    this.setState({title: title});

    fetch("/api/program")
    .then(response => response.json())
    .then(async programs => {
      this.indexPrograms(programs);
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });
  }

  render() {

    let content = <LoadingMessage/>;

    try {

      if (this.state.isError) {

        content = <ErrorMessage/>;

      } else if (this.state.isLoaded) {

        console.log("Filters: ");
        console.log(this.props.filters);
        this.applyFilters(this.props.filters)

        content =
            <ProgramList
                programs={this.state.data.dimensions.allData.top(Infinity)}
                title={this.state.title}
                filters={this.props.filters}
                user={this.props.user}
            />;

      }

    } catch (e) {
      console.error(e);
      content = <ErrorMessage/>
    }

    return (
        <React.Fragment>
          <div className="wrapper">
            <SideBar/>
            <div className="main">
              <NavBar user={this.props.user}/>
              <div className="content">
                {content}
              </div>
              <Footer/>
            </div>
          </div>
          <ProgramFilters/>
        </React.Fragment>
    );

  }

}

export default compose(
    withRouter,
    connect(
        store => ({
          filters: store.filters,
          user: store.user
        })
    )
)(ProgramListView);
