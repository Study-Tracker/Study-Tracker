import React from "react";

const TaskInputList = props => {

  const handleContainerLoaded = container => {
    if (container) {
      props.onContainerLoaded(container);
    }
  }

  return (
      <div id="task-input-container" ref={handleContainerLoaded}>
        {props.children}
      </div>
  )

}

export default TaskInputList;