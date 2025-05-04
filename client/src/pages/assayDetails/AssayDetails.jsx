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
import {Col, Container, Row, Tab, Tabs} from "react-bootstrap";
import AssayTimelineTab from "./AssayTimelineTab";
import AssayNotebookTab from "./AssayNotebookTab";
import {Breadcrumbs} from "../../common/common";
import PropTypes from "prop-types";
import {useLocation, useNavigate} from "react-router-dom";
import AssayDetailsHeader from "./AssayDetailsHeader";
import AssayOverviewTab from "./AssayOverviewTab";
import AssayFileManagerTab from "./AssayFileManagerTab";
import AssayTasksTab from "./AssayTasksTab";
import {useDispatch, useSelector} from "react-redux";
import {setTab} from "../../redux/tabSlice";

const AssayDetails = props => {

  const location = useLocation();
  const navigate = useNavigate();
  const {user, study, features, assay} = props;
  const tab = useSelector(s => s.tab.value) || location.hash.replace("#", "") || "overview";
  const dispatch = useDispatch();

  const handleTabSelect = (key) => {
    dispatch(setTab(key));
    navigate("#" + key);
  }

  return (
      <Container fluid className="animated fadeIn">

        <Row>
          <Col>
            <Breadcrumbs crumbs={[
              {label: "Home", url: "/"},
              {label: "Study " + study.code, url: `/study/${study.code}#assays`},
              {label: "Assay " + assay.code}
            ]}/>
          </Col>
        </Row>

        <AssayDetailsHeader
            assay={assay}
            study={study}
        />

        <Row>

          <Col md={12}>

            <Tabs
                variant={"pills"}
                activeKey={tab}
                onSelect={handleTabSelect}
            >

              <Tab eventKey={"overview"} title={"Overview"}>
                <AssayOverviewTab
                  assay={assay}
                  handleTabSelect={handleTabSelect}
                  study={study}
                />
              </Tab>

              <Tab eventKey={"tasks"} title={"Tasks"}>
                <AssayTasksTab assay={assay} user={user} />
              </Tab>

              <Tab eventKey={"timeline"} title={"Timeline"}>
                <AssayTimelineTab assay={assay} />
              </Tab>

              <Tab eventKey={"files"} title={"Files"}>
                <AssayFileManagerTab assay={assay} user={user} />
              </Tab>

              {
                features
                && features.notebook
                && features.notebook.isEnabled
                && assay.notebookFolder ? (
                    <Tab eventKey={"notebook"} title={"Notebook"}>
                      <AssayNotebookTab assay={assay} user={user}/>
                    </Tab>
                ) : ""
              }

            </Tabs>

          </Col>

        </Row>

      </Container>
  );

}

AssayDetails.propTypes = {
  user: PropTypes.object,
  study: PropTypes.object.isRequired,
  assay: PropTypes.object.isRequired,
  features: PropTypes.object,

}

export default AssayDetails;