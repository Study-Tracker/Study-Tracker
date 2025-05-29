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

import {CheckCircle} from "react-feather";
import StatWidget from "../../common/widgets/StatWidget";
import React from "react";
import PropTypes from "prop-types";
import IllustrationWidget from "../../common/widgets/IllustrationWidget";
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

const AssayTasksWidget = ({tasks, handleClick}) => {
  const activeCount = tasks.filter(t => t.status === 'TODO').length;
  const completedCount = tasks.filter(t => t.status === 'COMPLETE').length;
  // const incompleteCount = tasks.filter(t => t.status === 'INCOMPLETE').length;
  const isEmpty = tasks.length === 0;
  const isComplete = completedCount === tasks.length;
  const navigate = useNavigate();

  if (isEmpty) {
    return (
        <IllustrationWidget
            image={"/static/images/clip/focused-working.png"}
            header={"No current tasks"}
            body={(
                <Button
                    color={"primary"}
                    onClick={() => {
                      navigate("#tasks");
                      navigate(0);
                    }}
                >
                  Add a task
                </Button>
            )}
            color={"dark"}
        />
    );
  }
  else if (isComplete) {
    return (
        <IllustrationWidget
            image={"/static/images/clip/completed-task.png"}
            header={"Nice work!"}
            body={"All tasks have been completed."}
        />
    )
  }
  return (
      <StatWidget
          label={"Active Tasks"}
          icon={CheckCircle}
          value={"" + activeCount}
          color={"info"}
          onClick={handleClick}
      />
  )
}

AssayTasksWidget.propTypes = {
  tasks: PropTypes.array.isRequired,
  handleClick: PropTypes.func.isRequired
}

export default AssayTasksWidget;
