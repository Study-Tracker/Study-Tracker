import React from "react";
import PropTypes from "prop-types";

const AssayTypeFieldInputList = props => {

  const handleContainerLoaded = container => {
    if (container) {
      props.onContainerLoaded(container);
    }
  }

  return (
      <div
          id="field-input-container"
          ref={handleContainerLoaded}
          className={props.isInvalid ? "is-invalid" : ""}
      >
        {props.children}
      </div>
  )

}

AssayTypeFieldInputList.propTypes = {
  onContainerLoaded: PropTypes.func.isRequired,
  isInvalid: PropTypes.bool
}

export default AssayTypeFieldInputList;