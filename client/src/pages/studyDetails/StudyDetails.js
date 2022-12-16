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

import {Breadcrumb, Col, Container, Row, Tab, Tabs} from 'react-bootstrap';
import React, {useState} from "react";
import StudyAssaysTab from "./StudyAssaysTab";
import StudyNotebookTab from './StudyNotebookTab';
import StudyConclusionsTab from "./StudyConclusionsTab";
import StudyCommentsTab from "./StudyCommentsTab";
import StudyTimelineTab from "./StudyTimelineTab";
import swal from "sweetalert";
import AddToStudyCollectionModal
  from "../../common/modals/AddToStudyCollectionModal";
import StudyCollectionsTab from "./StudyCollectionsTab";
import PropTypes from "prop-types";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import StudyDetailHeader from "./StudyDetailsHeader";
import StudyOverviewTab from "./StudyOverviewTab";
import StudyFileManagerTab from "./StudyFileManagerTab";

const StudyDetails = props => {

  const {study, user, features} = props;
  const [showCollectionModal, setShowCollectionModal] = useState(false);
  const [modalError, setModalError] = useState(null);
  const navigate = useNavigate();

  const handleStudyDelete = () => {
    swal({
      title: "Are you sure you want to remove this study?",
      text: "Removed studies will be hidden from view, but their records will not be deleted. Studies can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios({
          url: "/api/internal/study/" + study.code,
          method: 'delete',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(response => {
          navigate("/studies")
        })
        .catch(error => {
          console.error(error);
          setModalError("Failed to remove study. Please try again.");
        })
      }
    });
  }

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
              <Breadcrumb.Item active>Study Detail</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <StudyDetailHeader
            study={study}
            handleAddToCollection={() => setShowCollectionModal(true)}
            handleDelete={handleStudyDelete}
        />

        <Row>

          <Col md={12}>

            {/* Tabs */}
            <Tabs variant={"pills"} defaultActiveKey={"overview"}>

              <Tab eventKey={"overview"} title={"Overview"}>
                <StudyOverviewTab
                    study={study}
                    user={user}
                    features={features}
                    handleAddToCollection={() => setShowCollectionModal(true)}
                />
              </Tab>

              <Tab eventKey={"assays"} title={"Assays"}>
                <StudyAssaysTab study={study} user={user}/>
              </Tab>

              {/*<Tab eventKey={"files"} title={"Files"}>*/}
              {/*  <StudyFilesTab study={study} user={user}/>*/}
              {/*</Tab>*/}

              <Tab title={"Files"} eventKey={"files"}>
                <StudyFileManagerTab study={study} user={user}/>
              </Tab>

              {
                features
                && features.notebook
                && features.notebook.isEnabled
                && study.notebookFolder ? (
                    <Tab eventKey={"notebook"} title={"Notebook"}>
                      <StudyNotebookTab study={study} user={user}/>
                    </Tab>
                ) : ""
              }

              <Tab eventKey={"conclusions"} title={"Conclusions"}>
                <StudyConclusionsTab study={study} user={user}/>
              </Tab>

              <Tab eventKey={"comments"} title={"Comments"}>
                <StudyCommentsTab study={study} user={user}/>
              </Tab>

              <Tab title={"Timeline"} eventKey={"timeline"}>
                <StudyTimelineTab study={study} user={user}/>
              </Tab>

              <Tab title={"Collections"} eventKey={"collections"}>
                <StudyCollectionsTab
                    study={study}
                    showCollectionModal={setShowCollectionModal}
                />
              </Tab>

            </Tabs>

          </Col>
        </Row>

        {/* Add to collection modal */}
        <AddToStudyCollectionModal
            showModal={setShowCollectionModal}
            isOpen={showCollectionModal}
            study={study}
        />

      </Container>
  );

}

StudyDetails.propTypes = {
  user: PropTypes.object,
  study: PropTypes.object.isRequired,
  features: PropTypes.object,
}

export default StudyDetails;