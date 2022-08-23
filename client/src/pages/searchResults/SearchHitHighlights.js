import {Col, Row} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const SearchHitHighlights = ({hit}) => {
  let list = [];
  for (const [field, highlight] of Object.entries(hit.highlightFields)) {
    highlight.forEach((h, i) => {
      const text = h.replace("<em>", "<mark>").replace("</em>", "</mark>");
      list.push(
          <SearchHitHighlight
              key={"search-highlight-" + hit.document.id + "-" + field + "-"
                  + i}
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

SearchHitHighlights.propTypes = {
  hit: PropTypes.object.isRequired
}

const SearchHitHighlight = ({field, text}) => {
  return (
      <Col lg={12} className={"search-hit-highlight"}>
        <h6>{field}</h6>
        <blockquote>
          <div className="bg-light p-2 font-italic"
               dangerouslySetInnerHTML={createMarkup(text)}/>
        </blockquote>
      </Col>
  )
}

SearchHitHighlight.propTypes = {
  field: PropTypes.string.isRequired,
  text: PropTypes.string.isRequired
}

const createMarkup = (content) => {
  return {__html: content};
};

export default SearchHitHighlights;