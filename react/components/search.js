import React, {useState} from 'react';
import {Card, Col, Container, Row} from "react-bootstrap";

export const SearchHits = ({hits}) => {

  let content = (
      <Row>
        <Col lg="12">
          <Card className={"illustration"}>
            <Card.Body>
              <div className="alert-message">
                <h4 className="alert-heading">Your search did not return any results.</h4>
                <p>Try broadening your search and try again.</p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
  );

  if (hits.hits.length > 0) {

    const list = hits.hits
    .filter(h => !!h.document.active)
    .sort((a, b) => {
      if (a.score < b.score) return 1;
      if (a.score > b.score) return -1;
      return 0;
    })
    .map((hit, i) => <SearchHit key={"search-hit-" + i} hit={hit}/>);

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

const SearchHitHighlight = ({field, text}) => {
  return (
      <Col lg={12} className={"search-hit-highlight"}>
        <h6>{field}</h6>
        <blockquote>
          <div className="bg-light p-2 font-italic" dangerouslySetInnerHTML={createMarkup(text)}/>
        </blockquote>
      </Col>
  )
}

const SearchHitHighlights = ({hit}) => {
  let list = [];
  for (const [field, highlight] of Object.entries(hit.highlightFields)) {
    highlight.forEach((h, i) => {
      const text = h.replace("<em>", "<mark>").replace("</em>", "</mark>");
      list.push(
          <SearchHitHighlight
            key={"search-highlight-" + hit.document.id + "-" + field + "-" + i}
            field={field}
            text={text}
          />
      );
    });
  }
  return (
    <Col sm={12}>
      <h6>Matched Fields</h6>
      <Row>
        {list}
      </Row>
    </Col>
  );
}

const createMarkup = (content) => {
  return {__html: content};
};

const SearchHit = ({hit}) => {
  const [toggle, setToggle] = useState(false);
  const study = hit.document;
  return (
      <Card>
        <Card.Body>
          <Row>

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
              <p dangerouslySetInnerHTML={createMarkup(study.description)} />
            </Col>

            <Col xs={12}>
              <a href={"/study/" + study.code} className="btn btn-sm btn-outline-primary">
                View Study
              </a>
              &nbsp;&nbsp;
              <a className="btn btn-sm btn-outline-secondary" onClick={() => setToggle(!toggle)}>
                {!!toggle ? "Hide": "Show"} Hit Details
              </a>
            </Col>

            <div hidden={!toggle} style={{width: "100%"}}>

              <Col xs={12}>
                <hr />
              </Col>

              <Col xs={12}>
                <h6>Search Score</h6>
                <p>{hit.score}</p>
              </Col>

              {
                !!hit.highlightFields
                    ? <SearchHitHighlights hit={hit} />
                    : ''
              }

            </div>

          </Row>
        </Card.Body>
      </Card>
  )
}