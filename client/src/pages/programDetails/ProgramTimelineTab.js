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

import React, {useEffect, useState} from "react";
import {Col, Row} from 'react-bootstrap'
import {ActivityStream} from "../../common/activity";
import {CardLoadingMessage} from "../../common/loading";
import {DismissableAlert} from "../../common/errors";
import axios from "axios";
import PropTypes from "prop-types";

const ProgramTimelineTab = props => {

  const {program} = props;
  const [activity, setActivity] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get("/api/internal/program/" + program.id + "/activity")
    .then(response => {
      setActivity(response.data);
    })
    .catch(e => {
      console.error(e);
      setError(e);
    })
  }, []);

  let content = <CardLoadingMessage/>;
  if (!!error) {
    content = <DismissableAlert color={'warning'}
                                message={'Failed to load program activity.'}/>;
  } else if (!!activity) {
    content = <ActivityStream activity={activity}/>;
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

ProgramTimelineTab.propTypes = {
  program: PropTypes.object.isRequired
}

export default ProgramTimelineTab;