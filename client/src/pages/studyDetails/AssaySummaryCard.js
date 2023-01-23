import React from "react";
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";
import {StatusBadge} from "../../common/status";

const AssaySummaryCard = ({study, assay}) => {

  return (
    <Card>
      <Card.Body>
        <Row>

          <Col className={"d-flex align-items-center"}>
            <div>
              <span className={"text-lg"}>
                <a href={"/study/" + study.code + "/assay/" + assay.code}>
                  {assay.name}
                </a>
              </span>
              <br />
              <span className={"text-muted fw-bolder"}>
                {assay.code}
              </span>
            </div>
          </Col>

          <Col>
            <span className="text-muted">Type</span>
            <br />
            <span className={"fw-bolder"}>
              {assay.assayType.name}
            </span>
          </Col>

          <Col>
            <span className="text-muted">Status</span>
            <br />
            <StatusBadge status={assay.status} />
          </Col>

          <Col>
            <span className="text-muted">Owner</span>
            <br />
            <span className={"fw-bolder"}>
              {assay.owner.displayName}
            </span>
          </Col>

          <Col>
            <span className="text-muted">Last Updated</span>
            <br />
            {new Date(assay.updatedAt ? assay.updatedAt : assay.createdAt).toLocaleDateString()}
          </Col>

        </Row>
      </Card.Body>
    </Card>
  );

}

AssaySummaryCard.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired
}

export default AssaySummaryCard;

