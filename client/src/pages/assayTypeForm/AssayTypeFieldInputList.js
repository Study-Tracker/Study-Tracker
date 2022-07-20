import React from "react";
import PropTypes from "prop-types";

const AssayTypeFieldInputList = props => {

  const handleContainerLoaded = container => {
    if (container) {
      props.onContainerLoaded(container);
    }
  }

  return (
      <div id="field-input-container" ref={handleContainerLoaded}>
        {props.children}
      </div>
  )

}

AssayTypeFieldInputList.propTypes = {
  onContainerLoaded: PropTypes.func.isRequired
}

export default AssayTypeFieldInputList;