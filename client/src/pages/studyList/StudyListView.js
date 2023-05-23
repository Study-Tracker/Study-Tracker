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

import React, {useEffect, useState} from "react";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import StudyList from "./StudyList";
import crossfilter from "crossfilter2";
import StudyFilters, {labels as filter} from '../../common/filters/StudyFilters'
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import {useSelector} from "react-redux";
import axios from "axios";

const StudyListView = props => {

  const user = useSelector(state => state.user.value);
  const filters = useSelector(state => state.filters.value);
  const [studyData, setStudyData] = useState(null);
  const [error, setError] = useState(null);

  const indexStudies = (studies) => {
    const data = {};
    data.cf = crossfilter(studies);
    data.dimensions = {};
    data.dimensions.allData = data.cf.dimension(d => d);
    data.dimensions[filter.PROGRAM] = data.cf.dimension(d => d.program.id);
    data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.legacy);
    data.dimensions[filter.EXTERNAL] = data.cf.dimension(
        d => !!d.collaborator);
    data.dimensions[filter.MY_STUDY] = data.cf.dimension(d => {
      if (!!user) {
        if (d.owner.id === user.id) return true;
        else {
          for (const u of d.users) {
            if (u.id === user.id) return true;
          }
        }
      }
      return false;
    });
    data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);
    data.dimensions[filter.ACTIVE] = data.cf.dimension(d => d.active);
    setStudyData(data);
  }

  const applyFilters = (filters) => {
    for (let key of Object.keys(studyData.dimensions)) {
      studyData.dimensions[key].filterAll();
      if (filters.hasOwnProperty(key) && filters[key] != null) {
        console.debug("Applying filter", key, filters[key]);
        if (Array.isArray(filters[key])) {
          studyData.dimensions[key].filter(
              d => filters[key].indexOf(d) > -1);
        } else {
          studyData.dimensions[key].filter(filters[key]);
        }
      }
    }
  }

  useEffect(() => {

    axios.get("/api/internal/study")
    .then(response => {
      console.debug("Studies", response.data);
      indexStudies(response.data);
    })
    .catch(error => {
      console.error(error);
      setError(error);
    });
  }, [user]);


  let content = <LoadingMessage/>;

  try {

    if (error) {

      content = <ErrorMessage/>;

    } else if (studyData) {

      // Apply filters
      console.debug("Active filters", filters);
      applyFilters(filters);

      content = (
          <StudyList
              studies={studyData.dimensions.allData.top(Infinity)}
              filters={filters}
              user={user}
          />
      );

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
            <NavBar />
            <div className="content">
              {content}
            </div>
            <Footer/>
          </div>
        </div>
        <StudyFilters />
      </React.Fragment>
  );

}

export default StudyListView;
