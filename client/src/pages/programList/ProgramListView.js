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

import React, {useContext, useEffect, useState} from "react";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import crossfilter from "crossfilter2";
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import ProgramFilters, {
  labels as filter
} from "../../common/filters/ProgramFilters";
import {useSelector} from "react-redux";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import {Col, Container, Form, FormGroup, Row} from "react-bootstrap";
import ProgramSummaryCard from "./ProgramSummaryCard";
import ProgramPlaceholder from "./ProgramPlaceholder";

const ProgramListView = () => {

  const filters = useSelector(state => state.filters.value);
  const [programData, setProgramData] = useState(null);
  const [search, setSearch] = useState("");
  const [error, setError] = useState(null);
  const notyf = useContext(NotyfContext);

  const indexPrograms = (programs) => {
    console.debug("Programs", programs);
    const data = {};
    data.cf = crossfilter(programs);
    data.dimensions = {};
    data.dimensions.allData = data.cf.dimension(d => d);
    data.dimensions.text = data.cf.dimension(d => d.name.toLowerCase() + " " + d.description.toLowerCase());
    data.dimensions[filter.ACTIVE] = data.cf.dimension(d => d.active)
    data.dimensions[filter.INACTIVE] = data.cf.dimension(d => !d.active)
    setProgramData(data);
  }

  const applyFilters = (filters) => {
    for (let key of Object.keys(programData.dimensions)) {
      programData.dimensions[key].filterAll();
      if (Object.prototype.hasOwnProperty.call(filters, key) && filters[key] != null) {
        if (Array.isArray(filters[key])) {
          programData.dimensions[key].filter(d => filters[key].indexOf(d) > -1);
        } else {
          programData.dimensions[key].filter(filters[key]);
        }
      }
    }
    if (search) {
      programData.dimensions.text.filter(d => d.indexOf(search.toLowerCase()) > -1);
    }
  }

  useEffect(() => {
    axios.get("/api/internal/program?details=true")
    .then(async response => {
      indexPrograms(response.data);
    })
    .catch(error => {
      console.error(error);
      setError(error);
      notyf.open({
        type: "error",
        message: "Error loading programs"
      })
    });
  }, [notyf]);

  let programs = [];
  if (programData) {
    applyFilters(filters);
    programs = programData.dimensions.allData.top(Infinity);
  }

  return (
      <React.Fragment>
        <div className="wrapper">
          <SideBar/>
          <div className="main">
            <NavBar />
            <div className="content">

              {
                !programData && !error && <LoadingMessage/>
              }

              {
                error && <ErrorMessage/>
              }

              {
                programData && !error && (
                      <Container fluid className="animated fadeIn">

                        <Row className="justify-content-between align-items-center mb-2">

                          <Col>
                            <h3>Programs</h3>
                          </Col>

                          <Col xs={"auto"}>
                            <FormGroup>
                              <Form.Control
                                type="input"
                                placeholder="Search"
                                value={search}
                                onChange={e => setSearch(e.target.value)}
                              />
                            </FormGroup>
                          </Col>
                        </Row>

                        {
                          programs.length === 0 && (
                            <ProgramPlaceholder />
                          )
                        }

                        <Row>
                          {
                            programs
                            .sort((a, b) => a.name.localeCompare(b.name))
                            .map(p => {
                              return <ProgramSummaryCard program={p} key={p.id}/>;
                            })
                          }
                        </Row>

                      </Container>
                )
              }

            </div>
            <Footer/>
          </div>
        </div>
        <ProgramFilters/>
      </React.Fragment>
  );

}

export default ProgramListView;
