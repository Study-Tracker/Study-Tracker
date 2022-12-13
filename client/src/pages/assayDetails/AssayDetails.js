/*
 * Copyright 2022 the original author or authors.
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
import AssayFilesTab from "./AssayFilesTab";
import AssayNotebookTab from "./AssayNotebookTab";
import swal from "sweetalert";
import {Breadcrumbs} from "../../common/common";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import AssayDetailsHeader from "./AssayDetailsHeader";
import AssayOverviewTab from "./AssayOverviewTab";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayDetails = props => {

  const {user, study, features} = props;
  const [assay, setAssay] = React.useState(props.assay);
  const [error, setError] = React.useState(null);
  const navigate = useNavigate();

  const handleAssayDelete = () => {
    swal({
      title: "Are you sure you want to remove this assay?",
      text: "Removed assays will be hidden from view, but their records will not be deleted. Assays can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/assay/" + assay.code)
        .then(response => {
          navigate("/assays")
        })
        .catch(error => {
          console.error(error);
          setError("Failed to remove study. Please try again.");
        })
      }
    });
  }

  return (
      <Container fluid className="animated fadeIn">

        <Row>
          <Col>
            <Breadcrumbs crumbs={[
              {label: "Home", url: "/"},
              {label: "Study " + study.code, url: "/study/" + study.code},
              {label: "Assay Details"}
            ]}/>
          </Col>
        </Row>

        <AssayDetailsHeader
            assay={assay}
            study={study}
            handleDelete={handleAssayDelete}
        />

        <Row>

          <Col md={12}>

            <Tabs variant={"pills"} defaultActiveKey="overview">

              <Tab eventKey={"overview"} title={"Overview"}>
                <AssayOverviewTab
                  assay={assay}
                />
              </Tab>

              <Tab eventKey={"timeline"} title={"Timeline"}>
                <AssayTimelineTab assay={assay} />
              </Tab>

              <Tab eventKey={"files"} title={"Files"}>
                <AssayFilesTab assay={assay} user={user} />
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