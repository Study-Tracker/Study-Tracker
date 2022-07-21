import React from "react";
import PropTypes from "prop-types";

const TaskInputList = props => {

  const handleContainerLoaded = container => {
    if (container) {
      props.onContainerLoaded(container);
    }
  }

  return (
      <div
          id="task-input-container"
          ref={handleContainerLoaded}
          className={props.isInvalid ? "is-invalid" : ""}
      >
        {props.children}
      </div>
  )

}

TaskInputList.propTypes = {
  onContainerLoaded: PropTypes.func.isRequired,
  isInvalid: PropTypes.bool
}

export default TaskInputList;