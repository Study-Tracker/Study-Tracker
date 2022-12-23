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
import axios from "axios";
import {Card} from "react-bootstrap";
import Timeline from "../../common/detailsPage/Timeline";
import PropTypes from "prop-types";

const SummaryTimelineCard = ({study, assay}) => {

  const [activity, setActivity] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    let url = null;
    if (study) {
      url = "/api/internal/study/" + study.code + "/activity";
    } else if (assay) {
      url = "/api/internal/assay/" + assay.id + "/activity";
    }
    axios.get(url)
    .then(response => setActivity(response.data))
    .catch(e => {
      setError(e);
      console.error(e);
    })
  }, []);

  return (
      <Card>
        <Card.Body>
          <h5 className="card-title">Latest Activity</h5>
          <Timeline events={activity} />
        </Card.Body>
      </Card>
  )

}

SummaryTimelineCard.propTypes = {
  study: PropTypes.object,
  assay: PropTypes.object
}

export default SummaryTimelineCard;