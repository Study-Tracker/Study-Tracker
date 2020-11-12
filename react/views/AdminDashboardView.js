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
import {connect} from "react-redux";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import AdminDashboard from "../components/admin/AdminDashboard";

const qs = require('qs');

class AdminDashboardView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    if (!!params.active) {
      this.setState({active: params.active})
    }
  }

  render() {
    return (
        <div className="wrapper">
          <div className="main">
            <NavBar hideToggle={true} hideSearch={true}/>
            <div className="content">
              <AdminDashboard user={this.props.user}
                              active={this.state.active}/>
            </div>
            <Footer/>
          </div>
        </div>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(AdminDashboardView);
