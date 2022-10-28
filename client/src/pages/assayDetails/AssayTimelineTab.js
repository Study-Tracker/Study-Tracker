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

const AssayTimelineTab = props => {

  const {assay} = props;

  const [state, setState] = useState({
    isLoaded: false,
    isError: false,
  });

  useEffect(() => {
    axios.get("/api/internal/assay/" + assay.code + "/activity")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        activity: response.data,
        isLoaded: true
      }));
    })
    .catch(e => {
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: e.message
      }));
    })
  }, []);


  let content = <CardLoadingMessage/>;
  if (!!state.isLoaded && !!state.activity) {
    content = <ActivityStream activity={state.activity}/>;
  } else if (state.isError) {
    content = <DismissableAlert color={'warning'}
                                message={'Failed to load assay activity.'}/>;
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

AssayTimelineTab.propTypes = {
  assay: PropTypes.object.isRequired
}

export default AssayTimelineTab;