/*
 * Copyright 2023 the original author or authors.
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

import {Card, Col, Row} from "react-bootstrap";
import SearchHitHighlights from "./SearchHitHighlights";
import PropTypes from "prop-types";
import React, {useState} from "react";

const createMarkup = (content) => {
  return {__html: content};
};

const StudySearchHit = ({hit}) => {
  const [toggle, setToggle] = useState(false);
  const study = hit.document.data;
  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={12}>
              <p className="text-muted">Study</p>
            </Col>

            <Col sm={8} md={10}>
              <h4>
                <a href={"/study/" + study.code}>{study.code}: {study.name}</a>
              </h4>
            </Col>

            <Col sm={4} md={2}>
              <p className="text-muted">
                <span className="float-end">{study.program.name}</span>
              </p>
            </Col>

            <Col sm={12}>
              <p dangerouslySetInnerHTML={createMarkup(study.description)}/>
            </Col>

            <Col xs={12}>
              <a href={"/study/" + study.code}
                 className="btn btn-sm btn-outline-primary">
                View Study
              </a>
              &nbsp;&nbsp;
              <a className="btn btn-sm btn-outline-secondary"
                 onClick={() => setToggle(!toggle)}>
                {!!toggle ? "Hide" : "Show"} Hit Details
              </a>
            </Col>

            <div hidden={!toggle} style={{width: "100%"}}>

              <Col xs={12}>
                <hr/>
              </Col>

              <Col xs={12}>
                <h6>Search Score</h6>
                <p>{hit.score}</p>
              </Col>

              {
                !!hit.highlightFields
                    ? <SearchHitHighlights hit={hit}/>
                    : ''
              }

            </div>

          </Row>
        </Card.Body>
      </Card>
  )
}

StudySearchHit.propTypes = {
  hit: PropTypes.object.isRequired
}

export default StudySearchHit;
