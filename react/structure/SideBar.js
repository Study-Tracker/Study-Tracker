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
import PerfectScrollbar from "react-perfect-scrollbar";
import {withRouter} from "react-router-dom";
import {connect} from "react-redux";
import {Activity, Clipboard, Layers, List, Target, Users} from "react-feather";

class Sidebar extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  isActive(paths) {
    for (let i = 0; i < paths.length; i++) {
      if (location.pathname.indexOf(paths[i]) > 0
          || location.pathname === ("/" + paths[i])) {
        return "active";
      }
    }
    return "";
  }

  render() {
    const {sidebar} = this.props;

    return (
        <nav className={"sidebar" + (!sidebar.isOpen ? " collapsed" : "")}>
          <div className="sidebar-content">
            <PerfectScrollbar>

              <a className="sidebar-brand" href="/">
                <img className="img-fluid" alt="Study Tracker"
                     src="/static/images/logo.png"/>
              </a>

              <ul className="sidebar-nav">

                <li className="sidebar-header">Navigation</li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link " + this.isActive([""])} href={"/"}>
                    <Activity size={18} className="align-middle me-3"/>
                    <span className="align-middle">Activity</span>
                  </a>
                </li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link "  + this.isActive(["studies", "study"])} href={"/studies"}>
                    <Clipboard size={18} className="align-middle me-3"/>
                    <span className="align-middle">Studies</span>
                  </a>
                </li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link " + this.isActive(["assay", "assays"])} href={"/assays"}>
                    <Layers size={18} className="align-middle me-3"/>
                    <span className="align-middle">Assays</span>
                  </a>
                </li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link " + this.isActive(["program", "programs"])} href={"/programs"}>
                    <Target size={18} className="align-middle me-3"/>
                    <span className="align-middle">Programs</span>
                  </a>
                </li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link "  + this.isActive(["users", "user"])} href={"/users"}>
                    <Users size={18} className="align-middle me-3"/>
                    <span className="align-middle">Users</span>
                  </a>
                </li>

                <li className={"sidebar-item"}>
                  <a className={"sidebar-link " + this.isActive(["collections", "collection"])} href={"/collections"}>
                    <List size={18} className="align-middle me-3"/>
                    <span className="align-middle">Collections</span>
                  </a>
                </li>

              </ul>
            </PerfectScrollbar>
          </div>
        </nav>
    );
  }
}

export default withRouter(
    connect(store => ({
      sidebar: store.sidebar,
      layout: store.layout,
      user: store.user
    }))(Sidebar)
);