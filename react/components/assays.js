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
import {Col, Row} from "react-bootstrap";
import ErrorMessage from "../structure/ErrorMessage";
import LoadingMessage from "../structure/LoadingMessage";
import {StatusIcon} from "./status";
import {StudyTeam} from "./studyMetadata";
import dateFormat from "dateformat";

const createMarkup = (content) => {
  return {__html: content};
};

const AssaySummaryCard = ({studyCode, assay}) => {
  return (
      <div className="d-flex assay-card">

        <div className="stat stat-transparent">
          <StatusIcon status={assay.status}/>
        </div>

        <div className="flex-grow-1 ms-3">

          <Row>
            <Col xs={12}>

              <span className="float-end">
                <h5 className="text-muted">
                  {assay.assayType.name}
                </h5>
              </span>

              <h4>
                <a href={"/study/" + studyCode + "/assay/" + assay.code}>
                  {assay.name}
                </a>
              </h4>
              <h6>{assay.code}</h6>

            </Col>
          </Row>

          <Row>

            <Col xs={12}>
              <div className="bg-light p-3" dangerouslySetInnerHTML={createMarkup(assay.description)}/>
            </Col>

            <Col xs={12}>
              <p className="text-muted">
                Created {dateFormat(new Date(assay.createdAt), 'mm/dd/yy @ h:MM TT')}
                {/*{new Date(assay.createdAt).toLocaleString()}*/}
              </p>
            </Col>

            {
              !!assay.updatedAt ? (
                  <Col xs={12}>
                    <small className="text-muted">
                      Updated {dateFormat(new Date(assay.updatedAt), 'mm/dd/yy @ h:MM TT')}
                      {/*Created {new Date(assay.updatedAt).toLocaleString()}*/}
                    </small>
                  </Col>
              ) : ''
            }

          </Row>

          <Row className="mt-2">

            <Col xs={12} sm={6}>
              {/*<h6 className="details-label">Assay Team</h6>*/}
              <StudyTeam users={assay.users} owner={assay.owner}/>
            </Col>

          </Row>

          {/*<Row className="mt-2">*/}
          {/*  <Col>*/}
          {/*    <Button size="md" variant="outline-primary"*/}
          {/*            onClick={() => history.push(*/}
          {/*                "/study/" + studyCode + "/assay/" + assay.code)}>*/}
          {/*      Details*/}
          {/*      &nbsp;*/}
          {/*      <FontAwesomeIcon icon={faSignInAlt}/>*/}
          {/*    </Button>*/}
          {/*  </Col>*/}
          {/*</Row>*/}

        </div>
      </div>
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