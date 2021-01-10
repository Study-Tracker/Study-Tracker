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

import React from "react";
import {Col, Row} from 'reactstrap'
import {Timeline} from "../activity";
import {CardLoadingMessage} from "../loading";
import {DismissableAlert} from "../errors";

class ProgramTimelineTab extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false
    };
  }

  componentDidMount() {
    fetch("/api/program/" + this.props.program.id + "/activity")
    .then(response => response.json())
    .then(json => {
      this.setState({
        activity: json,
        isLoaded: true
      });
    })
    .catch(e => {
      this.setState({
        isError: true,
        error: e.message
      })
    })
  }

  render() {

    let content = <CardLoadingMessage/>;
    if (!!this.state.isLoaded && !!this.state.activity) {
      content = <Timeline activities={this.state.activity}/>;
    } else if (this.state.isError) {
      content = <DismissableAlert color={'warning'}
                                  message={'Failed to load program activity.'}/>;
    }

    return (
        <div className="timeline-tab">
          <Row>
            <Col sm={12}>
              {content}
            </Col>
          </Row>
        </div>
    )

  }

}

export default ProgramTimelineTab;