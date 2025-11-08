/*
 * Copyright 2019-2025 the original author or authors.
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
import {Form} from "react-bootstrap";
import PropTypes from "prop-types";
import DatePicker from "react-datepicker";
import Select from "react-select";
import axios from "axios";
import { useQuery } from "@tanstack/react-query";
import AsyncSelect from "react-select/async";

export const BenchlingFieldInput = ({field, value, handleUpdate}) => {
  switch(field.type) {
    case "text":
      return <BenchlingStringFieldInput field={field} value={value} handleUpdate={handleUpdate}/>;
    case "float":
    case "integer":
      return <BenchlingNumberFieldInput field={field} value={value} handleUpdate={handleUpdate}/>;
    case "boolean":
      return <BenchlingBooleanFieldInput field={field} value={value} handleUpdate={handleUpdate}/>;
    case "date":
    case "datetime":
      return <BenchlingDateFieldInput field={field} value={value} handleUpdate={handleUpdate}/>;
    case "dropdown":
      return <BenchlingDropdownFieldInput field={field} value={value} handleUpdate={handleUpdate}/>;
    case "entity_link":
      return <BenchlingCustomEntitySelectInput field={field} value={value} handleUpdate={handleUpdate}/>;
    default:
      return <p>Unsupported field type: {field.type}</p>;
  }
}
BenchlingFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

export const BenchlingStringFieldInput = ({field, value, handleUpdate}) => {
  return (
      <Form.Group>
        <Form.Label>{field.name}{field.isRequired ? " *" : ""}</Form.Label>
        <Form.Control
            type="text"
            defaultValue={value || ''}
            onChange={e => handleUpdate(field.name, e.target.value)}
        />
      </Form.Group>
  )
}
BenchlingStringFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

export const BenchlingNumberFieldInput = ({field, value, handleUpdate}) => {
  return (
    <Form.Group>
      <Form.Label>{field.name}{field.isRequired ? " *" : ""}</Form.Label>
      <Form.Control
        type="number"
        defaultValue={value}
        onChange={e => handleUpdate(field.name, e.target.value)}
      />
    </Form.Group>
  )
}
BenchlingNumberFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

export const BenchlingBooleanFieldInput = ({field, value, handleUpdate}) => {
  return (
    <Form.Group>
      <Form.Check
        type={"switch"}
        label={field.name}
        onChange={e => handleUpdate(field.name, e.target.checked)}
        defaultChecked={value}
      />
    </Form.Group>
  )
}
BenchlingBooleanFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

export const BenchlingDateFieldInput = ({field, handleUpdate}) => {
  const [value, setValue] = React.useState(null);
  return (
    <Form.Group>
      <Form.Label>{field.name}{field.isRequired ? " *" : ""}</Form.Label>
      <DatePicker
        maxlength="2"
        className={"form-control"}
        selected={value}
        wrapperClassName="form-control"
        name={field.name}
        onChange={(date) => {
          handleUpdate(field.name, date.toISOString());
          setValue(date);
        }}
        isClearable={true}
        dateFormat=" MM / dd / yyyy"
        placeholderText="MM / DD / YYYY"
      />
    </Form.Group>
  )
}
BenchlingDateFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

export const BenchlingDropdownFieldInput = ({field, value, handleUpdate}) => {

  const { data, error } = useQuery({
    queryKey: ["benchlingDropdown", field.dropdownId],
    queryFn: () => axios.get(`/api/internal/integrations/benchling/dropdowns/${field.dropdownId}`)
    .then(response => response.data.options),
    enabled: !!field.dropdownId,
    initialData: [],
  });

  if (!field.dropdownId || error) {
    return <p>Unable to populate dropdown for field {field.name}</p>
  }

  const options = data.map(o => { return { label: o.name, value: o.id }});

  return (
    <Form.Group>
      <Form.Label>{field.name}{field.isRequired ? " *" : ""}</Form.Label>
      <Select
        className={"react-select-container"}
        classNamePrefix="react-select"
        options={options}
        isMulti={field.isMulti}
        onChange={(selected) => handleUpdate(field.name, selected.value)}
        menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
        menuPosition="fixed"
        styles={{
          menuPortal: (base) => ({ ...base, zIndex: 9999 }),
        }}
      />
    </Form.Group>
  )
}
BenchlingDropdownFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}

const BenchlingCustomEntitySelectInput = ({field, value, handleUpdate}) => {

  const entityCallback = React.useCallback((input, callback) => {
    if (!input || input.length < 2) return callback([]);
    axios.get(`/api/internal/integrations/benchling/custom-entities?q=${input}${field.schemaId ? '&schemaId=' + field.schemaId : ''}`)
    .then(response => {
      const options = response.data
      .sort((a, b) => {
        if (a.name > b.name) {
          return 1;
        }
        if (a.name < b.name) {
          return -1;
        }
        return 0;
      })
      .map(t => {
        return {
          value: t.id,
          label: t.name,
          obj: t,
        };
      });
      callback(options);
    })
  }, [field.schemaId]);

  return (
    <Form.Group>
      <Form.Label>{field.name}{field.isRequired ? " *" : ""}</Form.Label>
      <AsyncSelect
        placeholder={"Type to search and select an entity..."}
        className={"react-select-container"}
        classNamePrefix="react-select"
        loadOptions={entityCallback}
        isMulti={field.isMulti}
        onChange={(selected) => {
          if (field.isMulti) {
            handleUpdate(field.name, selected.map(s => s.value));
          } else {
            handleUpdate(field.name, selected.value)
          }
        }}
        // defaultOptions={true}
        menuPortalTarget={document.body}
      />
    </Form.Group>
  )
}
BenchlingCustomEntitySelectInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.any,
  handleUpdate: PropTypes.func.isRequired,
}
