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
import {Button, Col, Form, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {Trash} from "react-feather";
import {FormGroup} from "./common";

const mapToAttributeArray = (map) => {
  let array = [];
  for (let key of Object.keys(map)) {
    array.push({
      key: key,
      value: map[key]
    });
  }
  return array;
}

const arrayToAttributeMap = (array) => {
  let map = {};
  for (let obj of array) {
    map[obj.key] = obj.value;
  }
  return map;
}

const AttributeInputs = props => {

  const [attributes, setAttributes] = React.useState(mapToAttributeArray(props.attributes));

  const handleValueUpdate = (key, value, index) => {
    const updated = [...attributes];
    updated[index] = {key: key, value: value};
    setAttributes(updated);
    props.handleUpdate(arrayToAttributeMap(updated));
  };

  const handleAddAttributeClick = () => {
    const newAttributes = [
      ...attributes,
      {key: "New Attribute", value: ""}
    ];
    setAttributes(newAttributes);
    props.handleUpdate(arrayToAttributeMap(newAttributes));
  }

  const handleRemoveAttributeClick = (index) => {
    let updated = [...attributes];
    updated.splice(index, 1);
    setAttributes(updated);
    props.handleUpdate(arrayToAttributeMap(updated));
  }

  return (
      <React.Fragment>

        {
          attributes.length > 0 && (
                <Row>
                  <Col xs={5}>
                    <FormGroup>
                      <Form.Label>Name</Form.Label>
                    </FormGroup>
                  </Col>
                  <Col xs={5}>
                    <FormGroup>
                      <Form.Label>Value</Form.Label>
                    </FormGroup>
                  </Col>
                  <Col xs={2}></Col>
                </Row>
          )
        }

        {
          attributes
          .filter(attribute => !attribute.key.startsWith("_"))
          .map((a, i) => {
            return (
                <Row key={'attributes-inputs-' + i}>
                  <Col xs={5}>
                    <FormGroup>
                      <Form.Label/>
                      <Form.Control
                          type="text"
                          value={a.key}
                          onChange={(e) => handleValueUpdate(e.target.value,
                              a.value, i)}
                      />
                    </FormGroup>
                  </Col>
                  <Col xs={5}>
                    <FormGroup>
                      <Form.Label/>
                      <Form.Control
                          type="text"
                          value={a.value}
                          onChange={(e) => handleValueUpdate(a.key,
                              e.target.value, i)}
                      />
                    </FormGroup>
                  </Col>
                  <Col xs={2}>
                    <a
                        className="text-danger"
                        title={"Remove attribute"}
                        onClick={() => handleRemoveAttributeClick(i)}
                    >
                      <Trash className="align-middle mt-4" size={18}/>
                    </a>
                  </Col>
                </Row>
            )
          })
        }

        {
          !!props.error
              ? (
                  <Row>
                    <Col>
                      <div className={"is-invalid"}></div>
                      <div className={"invalid-feedback"}>{props.error}</div>
                    </Col>
                  </Row>
              ) : ''

        }

        <Row>
          <Col md={12} className="mt-2">
            <Button
                variant="info"
                onClick={handleAddAttributeClick}
                className={"ps-5 pe-5"}
            >
              <FontAwesomeIcon icon={faPlusCircle}/> Add Attribute
            </Button>
          </Col>
        </Row>

      </React.Fragment>
  );


}

export default AttributeInputs;