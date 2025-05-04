/*
 * Copyright 2019-2023 the original author or authors.
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
import {Card, Col, Row} from 'react-bootstrap'
import {ActivityStream} from "../../common/activity";
import {CardLoadingMessage} from "../../common/loading";
import {DismissableAlert} from "../../common/errors";
import axios from "axios";
import PropTypes from "prop-types";
import {useQuery} from "@tanstack/react-query";

const AssayTimelineTab = ({assay}) => {

  const {data: activity, isLoading, error} = useQuery("activity", () => {
    return axios.get(`/api/internal/assay/${assay.code}/activity`)
    .then(response => response.data);
  });

  return (
      <Card>
        <Card.Body>
          <Row>
            <Col sm={12}>
              {
                isLoading ? (
                    <CardLoadingMessage/>
                ) : error ? (
                    <DismissableAlert
                        color={'warning'}
                        message={'Failed to load assay activity.'}
                    />
                ) : (
                    <ActivityStream activity={activity}/>
                )
              }
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )

}

AssayTimelineTab.propTypes = {
  assay: PropTypes.object.isRequired
}

export default AssayTimelineTab;