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
import {Card, Col, Container, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import StudySearchHit from "./StudySearchHit";
import AssaySearchHit from "./AssaySearchHit";

const SearchHits = ({hits}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center">
          <Col xs={12}>
            <h3>Search Results</h3>
          </Col>
        </Row>

        <Row>
          <Col xs={12}>
            {
              hits.length > 0 ? (
                  hits.filter(h => !!h.document.data.active)
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

                    })
                ) : (
                  <Card className={"illustration"}>
                    <Card.Body>
                      <div className="alert-message">
                        <h4 className="alert-heading">Your search did not return any
                          results.</h4>
                        <p>Try broadening your search and try again.</p>
                      </div>
                    </Card.Body>
                  </Card>
              )
            }
          </Col>
        </Row>

      </Container>
  )

}

SearchHits.propTypes = {
  hits: PropTypes.array.isRequired
}

export default SearchHits;

