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
import {
  Badge,
  Collapse,
  Form,
  Input,
  InputGroup,
  InputGroupAddon
} from "reactstrap";
import PerfectScrollbar from "react-perfect-scrollbar";
import {NavLink, withRouter} from "react-router-dom";
import sidebarRoutes from "../config/sidebarRoutes";
import {connect} from "react-redux";
import {Search} from "react-feather";

const SidebarCategory = withRouter(
    ({
      name,
      badgeColor,
      badgeText,
      icon: Icon,
      isOpen,
      children,
      onClick,
      location,
      to
    }) => {
      const getSidebarItemClass = path => {
        return location.pathname.indexOf(path) !== -1 ||
        (location.pathname === "/" && path === "/dashboard")
            ? "active"
            : "";
      };

      return (
          <li className={"sidebar-item " + getSidebarItemClass(to)}>
        <span
            data-toggle="collapse"
            className={"sidebar-link " + (!isOpen ? "collapsed" : "")}
            onClick={onClick}
            aria-expanded={isOpen ? "true" : "false"}
        >
          <Icon size={18} className="align-middle mr-3"/>
          <span className="align-middle">{name}</span>
          {badgeColor && badgeText ? (
              <Badge color={badgeColor} size={18} className="sidebar-badge">
                {badgeText}
              </Badge>
          ) : null}
        </span>
            <Collapse isOpen={isOpen}>
              <ul id="item" className={"sidebar-dropdown list-unstyled"}>
                {children}
              </ul>
            </Collapse>
          </li>
      );
    }
);

const SidebarItem = withRouter(
    ({name, badgeColor, badgeText, icon: Icon, location, to}) => {
      const getSidebarItemClass = path => {
        return location.pathname === path ? "active" : "";
      };

      return (
          <li className={"sidebar-item " + getSidebarItemClass(to)}>
            <NavLink to={to} className="sidebar-link" activeClassName="active">
              {Icon ? <Icon size={18} className="align-middle mr-3"/> : null}
              {name}
              {badgeColor && badgeText ? (
                  <Badge color={badgeColor} size={18} className="sidebar-badge">
                    {badgeText}
                  </Badge>
              ) : null}
            </NavLink>
          </li>
      );
    }
);

const SidebarLink = withRouter(({name, icon: Icon, location, to}) => {
  const getSidebarItemClass = path => {
    return location.pathname === path ? "active" : "";
  };
  return (
      <li className={"sidebar-item " + getSidebarItemClass(to)}>
        <a href={to} className="sidebar-link">
          {Icon ? <Icon size={18} className="align-middle mr-3"/> : null}
          {name}
        </a>
      </li>
  );
});

class Sidebar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  toggle = index => {
    // Collapse all elements
    Object.keys(this.state).forEach(
        item =>
            this.state[index] ||
            this.setState(() => ({
              [item]: false
            }))
    );

    // Toggle selected element
    this.setState(state => ({
      [index]: !state[index]
    }));
  };

  componentWillMount() {
    /* Open collapse element that matches current url */
    const pathName = this.props.location.pathname;

    sidebarRoutes.forEach((route, index) => {
      const isActive = pathName.indexOf(route.path) === 0;
      const isOpen = route.open;
      const isHome = route.containsHome && pathName === "/" ? true : false;

      this.setState(() => ({
        [index]: isActive || isOpen || isHome
      }));
    });
  }

  render() {
    const {sidebar} = this.props;

    return (
        <nav
            className={
              "sidebar" +
              (!sidebar.isOpen ? " toggled" : "") +
              (sidebar.isSticky ? " sidebar-sticky" : "")
            }
        >
          <div className="sidebar-content">
            <PerfectScrollbar>
              <a className="sidebar-brand" href="/">
                <img className="img-fluid" alt="Study Tracker"
                     src="/static/images/logo.png"/>
              </a>

              <ul className="sidebar-nav">

                {/*Search*/}
                <li className="sidebar-header">Search</li>

                <li className="sidebar-item">
                  <Form className="ml-3 mr-3">
                    <InputGroup className="mb-3 sidebar-search">
                      <Input
                          type="text"
                          placeholder="Enter keywords here..."
                          aria-label="Search"
                          className="form-control-no-border"
                          name={"search"}
                      />
                      <InputGroupAddon addonType={"append"}>
                        <button type={"submit"} className={"btn btn-primary"}>
                          <Search className={"feather align-middle"}/>
                        </button>
                      </InputGroupAddon>
                    </InputGroup>
                  </Form>
                </li>

                {sidebarRoutes.map((category, index) => {
                  return (
                      <React.Fragment key={index}>
                        {category.header ? (
                            <li className="sidebar-header">{category.header}</li>
                        ) : null}

                        {category.children ? (
                            <SidebarCategory
                                name={category.name}
                                badgeColor={category.badgeColor}
                                badgeText={category.badgeText}
                                icon={category.icon}
                                to={category.path}
                                isOpen={this.state[index]}
                                onClick={() => this.toggle(index)}
                            >
                              {
                                category.children.map((route, index) => {
                                  if (!!route.protected
                                      && !this.props.user) {
                                    return '';
                                  } else {
                                    return (
                                        <SidebarLink
                                            key={index}
                                            name={route.name}
                                            to={route.path}
                                            badgeColor={route.badgeColor}
                                            badgeText={route.badgeText}
                                        />
                                    );
                                  }
                                })
                              }
                            </SidebarCategory>
                        ) : (
                            <SidebarItem
                                name={category.name}
                                to={category.path}
                                icon={category.icon}
                                badgeColor={category.badgeColor}
                                badgeText={category.badgeText}
                            />
                        )}
                      </React.Fragment>
                  );
                })}
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