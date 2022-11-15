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
import dragula from "react-dragula";
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import AssayTypeFieldInputCard from "./AssayTypeFieldInputCard";
import AssayTypeFieldInputList from "./AssayTypeFieldInputList";
import PropTypes from "prop-types";

const AssayTypeFieldInputs = props => {

  const {handleUpdate, error} = props;
  const [fields, setFields] = useState(props.fields || []);
  const containers = [];

  useEffect(() => {
    dragula(containers);
  }, []);

  const onContainerReady = container => {
    console.debug("Container", container);
    containers.push(container);
  };

  const handleFieldUpdate = (data, index) => {
    let f = fields;
    f[index] = {
      ...f[index],
      ...data
    };
    setFields(f)
    handleUpdate(fields);
  }

  const handleAddFieldClick = () => {
    const newField = [
      ...fields,
      {
        displayName: "",
        fieldName: "",
        type: "STRING",
        description: "",
        required: false,
        fieldOrder: fields.length + 1
      }
    ];
    setFields(newField);
    handleUpdate(newField);
  }

  const handleRemoveFieldClick = (index) => {
    let updated = fields;
    updated.splice(index, 1);
    setFields(updated);
    handleUpdate(updated);
  }

  const cards = fields.map((field, index) => {
    return (
        <Row key={'field-inputs-' + index} data-index={index}>
          <Col xs={12}>
            <AssayTypeFieldInputCard
                field={field}
                index={index}
                handleRemoveField={handleRemoveFieldClick}
                handleFieldUpdate={handleFieldUpdate}
            />
          </Col>
        </Row>
    )
  });

  return (
      <React.Fragment>

        <AssayTypeFieldInputList
            isInvalid={!!error}
            onContainerLoaded={onContainerReady}
        >
          {cards}
        </AssayTypeFieldInputList>

        {
          !!error
              ? (<div className={"invalid-feedback"}>{error}</div>)
              : ''
        }

        <Row>
          <Col md={12}>
            <Button
                variant="info"
                onClick={handleAddFieldClick}>
              <FontAwesomeIcon icon={faPlusCircle}/> Add Field
            </Button>
          </Col>
        </Row>

      </React.Fragment>
  );

}

AssayTypeFieldInputs.propTypes = {
  fields: PropTypes.array,
  handleUpdate: PropTypes.func.isRequired
}

export default AssayTypeFieldInputs;