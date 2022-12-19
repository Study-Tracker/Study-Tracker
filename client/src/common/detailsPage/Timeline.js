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
import React from "react";
import PropTypes from "prop-types";
import dateFormat from "dateformat";

const toTitleCase = (str) => {
  return str.replaceAll("_", " ")
  .replace(
      /\w\S*/g,
      function(txt) {
        return txt.charAt(0).toUpperCase() + txt.substring(1).toLowerCase();
      }
  );
}

const TimelineItem = ({event}) => {
  return (
      <li className={"timeline-item d-flex align-items-start flex-column mb-2"}>
        <strong>
          {toTitleCase(event.eventType)}
        </strong>
        <span>by {event.user.displayName}</span>
        <span className={"text-muted text-sm"}>
          {dateFormat(new Date(event.date), 'mm/dd/yy @ h:MM TT')}
        </span>
      </li>
  )
}

const TimelineFooter = ({count, limit}) => {
  if (count > limit) {
    const remainder = count - limit;
    return (
        <li className={"timeline-item d-flex align-items-start flex-column"}>
          <strong>+ {remainder} more events</strong>
        </li>
    )
  } else {
    return null;
  }
}

const Timeline = ({events}) => {

  const [limit, setLimit] = React.useState(5);

  const items = events
  .sort((a, b) => new Date(b.date) - new Date(a.date))
  .filter((e, i) => i < limit)
  .map((event, i) => {
    return <TimelineItem event={event} key={i}/>
  });

  return (
    <ul className={"timeline"}>
      {items}
      <TimelineFooter count={events.length} limit={limit}/>
    </ul>
  )
}

Timeline.propTypes = {
  events: PropTypes.array.isRequired
}

export default Timeline;