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
import {Button, Col, Media, Row} from "reactstrap";
import ErrorMessage from "../structure/ErrorMessage";
import LoadingMessage from "../structure/LoadingMessage";
import {StatusIcon} from "./status";
import {history} from '../App';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons";
import {StudyTeam} from "./studyMetadata";

const createMarkup = (content) => {
  return {__html: content};
};

const AssaySummaryCard = ({studyCode, assay}) => {
  return (
      <Media className="assay-card">

        <StatusIcon status={assay.status}/>

        <Media body>

          <Row>
            <Col xs={12}>

              <span className="float-right">
                <h5>
                  {assay.assayType.name}
                </h5>
              </span>

              <h6>{assay.code}</h6>
              <h4>
                <a href={"/study/" + studyCode + "/assay/" + assay.code}>
                  {assay.name}
                </a>
              </h4>

            </Col>
          </Row>

          <Row>

            <Col xs={12}>
              <h6 className="details-label">Description</h6>
              <div dangerouslySetInnerHTML={createMarkup(assay.description)}/>
            </Col>

          </Row>

          <Row className="mt-2">

            <Col sm={4}>
              <h6 className="details-label">Start Date</h6>
              <p>
                {new Date(assay.startDate).toLocaleDateString()}
              </p>
            </Col>

            {
              !!assay.endDate ? (
                  <Col sm={4}>
                    <span>
                      <h6 className="details-label">End Date</h6>
                      <p>
                        {new Date(assay.endDate).toLocaleDateString()}
                      </p>
                    </span>
                  </Col>
              ) : ''
            }

            <Col sm={4}>
              <h6 className="details-label">Last Updated</h6>
              <p>
                {new Date(assay.updatedAt).toLocaleDateString()}
              </p>
            </Col>

          </Row>

          <Row className="mt-2">

            <Col xs={12} sm={6}>
              <h6 className="details-label">Assay Team</h6>
              <StudyTeam users={assay.users} owner={assay.owner}/>
            </Col>

          </Row>

          <Row className="mt-2">
            <Col>
              <Button outline size="md" color="primary"
                      onClick={() => history.push(
                          "/study/" + studyCode + "/assay/" + assay.code)}>
                Details
                &nbsp;
                <FontAwesomeIcon icon={faSignInAlt}/>
              </Button>
            </Col>
          </Row>

        </Media>
      </Media>
  );
};

export class AssaySummaryCards extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      assays: [],
      isLoaded: false,
      isError: false
    }
  }

  componentDidMount() {
    fetch("/api/study/" + this.props.studyCode + "/assays")
    .then(response => response.json())
    .then(assays => {
      console.log(assays);
      this.setState({
        assays: assays,
        isLoaded: true
      });
    })
    .catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    })
  }

  render() {

    let content = <LoadingMessage/>;
    if (!!this.state.error) {
      content = <ErrorMessage/>;
    } else if (this.state.isLoaded) {
      content = [];
      if (this.state.assays.length === 0) {
        content.push(
            <Row className="text-center" key={"no-assay-message"}>
              <Col>
                <h4>This study does not have any assays</h4>
                <p>Click the 'New Assay' button to register a new one.</p>
              </Col>
            </Row>
        );
      } else {
        for (let i = 0; i < this.state.assays.length; i++) {
          let assay = this.state.assays[i];
          if (i > 0) {
            content.push(<hr key={"assay-border-" + assay.id}/>);
          }
          content.push(
              <AssaySummaryCard
                  key={"assay-card-" + assay.id}
                  studyCode={this.props.studyCode}
                  assay={assay}
              />
          );
        }
      }
    }

    return (
        <div>
          {content}
        </div>
    );
  }
}