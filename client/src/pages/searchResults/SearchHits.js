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

import React, {useState} from "react";
import {Card, Col, Container, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import SearchHitHighlights from "./SearchHitHighlights";

const SearchHits = ({hits}) => {

  let content = (
      <Row>
        <Col lg="12">
          <Card className={"illustration"}>
            <Card.Body>
              <div className="alert-message">
                <h4 className="alert-heading">Your search did not return any
                  results.</h4>
                <p>Try broadening your search and try again.</p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
  );

  if (hits.hits.length > 0) {

    const list = hits.hits
    .filter(h => !!h.document.data.active)
    .sort((a, b) => {
      if (a.score < b.score) {
        return 1;
      }
      if (a.score > b.score) {
        return -1;
      }
      return 0;
    })
    .map((hit, i) => {
      if (hit.document.type === "STUDY") {
        return <StudySearchHit key={"search-hit-" + i} hit={hit}/>
      } else {
        return <AssaySearchHit key={"search-hit-" + i} hit={hit}/>
      }

    });

    content = (
        <Row>
          <Col lg={12}>
            {list}
          </Col>
        </Row>
    )

  }

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center">
          <Col xs={12}>
            <h3>Search Results</h3>
          </Col>
        </Row>

        {content}

      </Container>
  )

}

SearchHits.propTypes = {
  hits: PropTypes.array.isRequired
}

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

const AssaySearchHit = ({hit}) => {
  const [toggle, setToggle] = useState(false);
  const assay = hit.document.data;
  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={12}>
              <p className="text-muted">Assay</p>
            </Col>

            <Col sm={8} md={10}>
              <h4>
                <a href={"/study/" + assay.study.code + "/assay/" + assay.code}>{assay.code}: {assay.name}</a>
              </h4>
            </Col>

            <Col sm={4} md={2}>
              <p className="text-muted">
                <span className="float-end">{assay.assayType.name}</span>
              </p>
            </Col>

            <Col sm={12}>
              <p dangerouslySetInnerHTML={createMarkup(assay.description)}/>
            </Col>

            <Col xs={12}>
              <a href={"/study/" + assay.study.code + "/assay/" + assay.code}
                 className="btn btn-sm btn-outline-primary">
                View Assay
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

AssaySearchHit.propTypes = {
  hit: PropTypes.object.isRequired
}

export default SearchHits;

