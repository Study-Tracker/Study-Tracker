/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import PerfectScrollbar from "react-perfect-scrollbar";
import {
  Activity,
  Clipboard,
  Folder,
  Layers,
  List,
  Target,
  Users
} from "react-feather";
import {useLocation} from "react-router-dom";
import {useSelector} from "react-redux";

const Sidebar = props => {

  const location = useLocation();
  const sidebarIsOpen = useSelector(state => state.sidebar.sidebarIsOpen);

  const isActive = (paths) => {
    let val = "";
    for (let i = 0; i < paths.length; i++) {
      let bit = paths[i];
      let inverse = false;
      if (bit.startsWith("!")) {
        inverse = true;
        bit = bit.substring(1);
      }
      if (location.pathname.indexOf(bit) > 0
          || location.pathname === ("/" + bit)) {
        if (inverse) {
          return ""
        } else {
          val = "active";
        }
      }
    }
    return val;
  }

  return (
      <nav className={"sidebar" + (!sidebarIsOpen ? " collapsed" : "")}>
        <div className="sidebar-content">
          <PerfectScrollbar>

            <a className="sidebar-brand" href="/">
              <img className="img-fluid" alt="Study Tracker"
                   src="/static/images/logo.png"/>
            </a>

            <ul className="sidebar-nav">

              <li className="sidebar-header">Navigation</li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive([""])}
                   href={"/"}>
                  <Activity size={18} className="align-middle me-3"/>
                  <span className="align-middle">Activity</span>
                </a>
              </li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["studies", "study", "!assay", "!assays"])} href={"/studies"}>
                  <Clipboard size={18} className="align-middle me-3"/>
                  <span className="align-middle">Studies</span>
                </a>
              </li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["assay", "assays"])} href={"/assays"}>
                  <Layers size={18} className="align-middle me-3"/>
                  <span className="align-middle">Assays</span>
                </a>
              </li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["program", "programs"])} href={"/programs"}>
                  <Target size={18} className="align-middle me-3"/>
                  <span className="align-middle">Programs</span>
                </a>
              </li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["users", "user"])} href={"/users"}>
                  <Users size={18} className="align-middle me-3"/>
                  <span className="align-middle">Users</span>
                </a>
              </li>

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["collections", "collection"])} href={"/collections"}>
                  <List size={18} className="align-middle me-3"/>
                  <span className="align-middle">Collections</span>
                </a>
              </li>

              {/*<li className="sidebar-header">Data Management</li>*/}

              <li className={"sidebar-item"}>
                <a className={"sidebar-link " + isActive(
                    ["file-manager"])} href={"/file-manager"}>
                  <Folder size={18} className="align-middle me-3"/>
                  <span className="align-middle">File Manager</span>
                </a>
              </li>

            </ul>
          </PerfectScrollbar>
        </div>
      </nav>
  );
}

export default Sidebar;