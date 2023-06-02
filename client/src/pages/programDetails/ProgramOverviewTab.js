import React from "react";
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import ProgramSummaryTimelineCard from "./ProgramSummaryTimelineCard";
import PrimaryStorageFolderWidget
  from "../../common/widgets/PrimaryStorageFolderWidget";
import PrimaryNotebookWidget from "../../common/widgets/PrimaryNotebookWidget";

const createMarkup = (content) => {
  return {__html: content};
};

const ProgramOverviewTab = ({program}) => {

  return (
      <Row>

        <Col md={8}>

          <Row>
            <Col xs={12}>
              {/* Summary Card */}
              <Card className="details-card">
                <Card.Body>

                  <Row>

                    <Col xs={12}>
                      <div className={"card-title h5"}>Summary</div>
                    </Col>

                    <Col md={12}>
                      <div dangerouslySetInnerHTML={createMarkup(program.description)} />
                    </Col>

                  </Row>

                  <Row>

                    <Col sm={4}>
                      <h6 className="details-label">Name</h6>
                      <p>{program.name}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Code</h6>
                      <p>{program.code}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Created</h6>
                      <p>{new Date(program.createdAt).toLocaleString()}</p>
                    </Col>

                    <Col md={12}>
                      <h6 className="details-label">Program Team</h6>
                      <TeamMembers users={program.users || []} />
                    </Col>

                  </Row>

                </Card.Body>
              </Card>
            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryStorageFolderWidget record={program} />
            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryNotebookWidget record={program} />
            </Col>

          </Row>

        </Col>

        <Col md={4}>
          <ProgramSummaryTimelineCard program={program} />
        </Col>

      </Row>
  )

}

ProgramOverviewTab.propTypes = {
  program: PropTypes.object.isRequired
}

export default ProgramOverviewTab;
