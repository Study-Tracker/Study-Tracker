/*
 * Copyright 2023 the original author or authors.
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
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import CustomFieldDefinitionCard from "./CustomFieldDefinitionCard";

const CustomFieldDefinitionDraggableCardList = ({fields, handleUpdate}) => {

  const handleFieldUpdate = (data, index) => {
    let f = fields;
    f[index] = {
      ...f[index],
      ...data
    };
    handleUpdate(fields);
  }

  const handleAddField = () => {
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
    handleUpdate(newField);
  }

  const handleRemoveField = (index) => {
    let updated = fields;
    updated.splice(index, 1);
    handleUpdate(updated);
  }

  const handleDragEnd = (result) => {
    console.debug("Drag end", result);
    if (!result.destination) {
      return;
    }
    if (result.destination.index === result.source.index) {
      return;
    }
    const updated = Array.from(fields);
    const [removed] = updated.splice(result.source.index, 1);
    updated.splice(result.destination.index, 0, removed);
    for (let i = 0; i < updated.length; i++) {
      updated[i].order = i;
    }
    handleUpdate(updated);
  }

  console.debug("Fields", fields);

  return (
      <>

        <DragDropContext onDragEnd={handleDragEnd}>
          <Droppable droppableId="draggable-field-list">
            {(provided) => (
                <div ref={provided.innerRef} {...provided.droppableProps}>
                  {
                    fields.sort((a, b) => a.order - b.order)
                    .map((field, index) => (
                      <Draggable
                          key={"draggable-field-" + index}
                          draggableId={"draggable-field-" + index}
                          index={index}
                      >
                        {(provided) => (
                          <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                          >
                            <CustomFieldDefinitionCard
                                field={field}
                                index={index}
                                handleFieldUpdate={handleFieldUpdate}
                                handleRemoveField={handleRemoveField}
                            />
                          </div>
                        )}
                      </Draggable>
                    ))
                  }
                </div>
            )}
          </Droppable>
        </DragDropContext>

        <Row>
          <Col md={12}>
            <Button
                variant="info"
                onClick={handleAddField}
                className={"ps-5 pe-5"}
            >
              <FontAwesomeIcon icon={faPlusCircle}/> Add Field
            </Button>
          </Col>
        </Row>

      </>
  )

}

CustomFieldDefinitionDraggableCardList.propTypes = {
  fields: PropTypes.array.isRequired,
  handleUpdate: PropTypes.func.isRequired
}

export default CustomFieldDefinitionDraggableCardList;