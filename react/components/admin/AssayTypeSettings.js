import React from 'react';
import {Card, CardBody, CardHeader, CardTitle} from "reactstrap";

class AssayTypeSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {}
  }

  render() {
    return (
        <React.Fragment>
          <AssayTypeListCard/>
        </React.Fragment>
    )
  }

}

export default AssayTypeSettings;

const AssayTypeListCard = ({}) => {
  return (
      <Card>
        <CardHeader>
          <CardTitle tag="h5" className="mb-0">
            Registered Assay Types
          </CardTitle>
        </CardHeader>
        <CardBody>
          <p>Coming soon...</p>
        </CardBody>
      </Card>
  )
}