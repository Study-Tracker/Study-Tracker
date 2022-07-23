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

import React, {useEffect, useState} from "react";
import {Col, Row} from "react-bootstrap";
import ErrorMessage from "./structure/ErrorMessage";
import LoadingMessage from "./structure/LoadingMessage";
import {StatusIcon} from "./status";
import {StudyTeam} from "./studyMetadata";
import dateFormat from "dateformat";
import axios from "axios";

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
              <div className="bg-light p-3"
                   dangerouslySetInnerHTML={createMarkup(assay.description)}/>
            </Col>

            <Col xs={12}>
              <p className="text-muted">
                Created {dateFormat(new Date(assay.createdAt),
                  'mm/dd/yy @ h:MM TT')}
                {/*{new Date(assay.createdAt).toLocaleString()}*/}
              </p>
            </Col>

            {
              !!assay.updatedAt ? (
                  <Col xs={12}>
                    <small className="text-muted">
                      Updated {dateFormat(new Date(assay.updatedAt),
                        'mm/dd/yy @ h:MM TT')}
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

        </div>
      </div>
  );
};

export const AssaySummaryCards = props => {

  const {studyCode} = props;
  const [assays, setAssays] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    axios.get("/api/study/" + studyCode + "/assays")
    .then(response => {
      console.debug(response.data);
      setAssays(response.data);
      setIsLoaded(true);
    })
    .catch(error => {
      setError(error);
      console.error(error);
    })
  }, [studyCode]);

  let content = <LoadingMessage/>;
  if (!!error) {
    content = <ErrorMessage/>;
  } else if (isLoaded) {
    content = [];
    if (assays.length === 0) {
      content.push(
          <Row className="text-center" key={"no-assay-message"}>
            <Col>
              <h4>This study does not have any assays</h4>
              <p>Click the 'New Assay' button to register a new one.</p>
            </Col>
          </Row>
      );
    } else {
      for (let i = 0; i < assays.length; i++) {
        let assay = assays[i];
        if (i > 0) {
          content.push(<hr key={"assay-border-" + assay.id}/>);
        }
        content.push(
            <AssaySummaryCard
                key={"assay-card-" + assay.id}
                studyCode={studyCode}
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