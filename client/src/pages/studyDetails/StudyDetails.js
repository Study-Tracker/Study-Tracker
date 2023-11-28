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

import {Breadcrumb, Col, Container, Row, Tab, Tabs} from 'react-bootstrap';
import React, {useState} from "react";
import StudyAssaysTab from "./StudyAssaysTab";
import StudyNotebookTab from './StudyNotebookTab';
import StudyConclusionsTab from "./StudyConclusionsTab";
import StudyCommentsTab from "./StudyCommentsTab";
import StudyTimelineTab from "./StudyTimelineTab";
import AddToStudyCollectionModal from "../../common/modals/AddToStudyCollectionModal";
import StudyCollectionsTab from "./StudyCollectionsTab";
import PropTypes from "prop-types";
import {useLocation, useNavigate} from "react-router-dom";
import StudyDetailHeader from "./StudyDetailsHeader";
import StudyOverviewTab from "./StudyOverviewTab";
import StudyFileManagerTab from "./StudyFileManagerTab";
import {useDispatch, useSelector} from "react-redux";
import {setTab} from "../../redux/tabSlice";

const StudyDetails = ({study, user, features}) => {

  const location = useLocation();
  const navigate = useNavigate();
  const [showCollectionModal, setShowCollectionModal] = useState(false);
  const tab = useSelector(s => s.tab.value) || location.hash.replace("#", "") || "overview";
  const dispatch = useDispatch();

  const handleTabSelect = (key) => {
    dispatch(setTab(key));
    navigate("#" + key);
  }

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
              <Breadcrumb.Item active>Study {study.code}</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <StudyDetailHeader
            study={study}
            handleAddToCollection={() => setShowCollectionModal(true)}
        />

        <Row>

          <Col md={12}>

            {/* Tabs */}
            <Tabs
                variant={"pills"}
                activeKey={tab}
                onSelect={handleTabSelect}
            >

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