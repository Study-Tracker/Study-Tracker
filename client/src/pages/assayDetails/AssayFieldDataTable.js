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

import {Button, Col, Row, Table} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile} from "@fortawesome/free-regular-svg-icons";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayFieldData = ({assay}) => {

  let fields = [];
  const assayTypeFields = assay.assayType.fields.sort((a, b) => {
    if (a.id > b.id) {
      return 1;
    } else if (a.id < b.id) {
      return -1;
    } else {
      return 0;
    }
  });
  const assayFields = assay.fields;
  for (let f of assayTypeFields) {
    if (assayFields.hasOwnProperty(f.fieldName)) {

      const value = assayFields[f.fieldName];

      if (["STRING", "INTEGER", "FLOAT", "DROPDOWN"].indexOf(f.type) > -1) {
        fields.push(
            <tr key={"assay-field-display-" + f.fieldName}>
              <td className="fw-bolder">{f.displayName}</td>
              <td>{value || 'n/a'}</td>
            </tr>
        );
      } else if (f.type === "TEXT") {
        fields.push(
            <tr key={"assay-field-display-" + f.fieldName}>
              <td className="fw-bolder">{f.displayName}</td>
              <td>
                <div dangerouslySetInnerHTML={createMarkup(
                  !!value ? value : 'n/a')}/>
              </td>
            </tr>
        )
      } else if (f.type === "BOOLEAN") {
        fields.push(
            <tr key={"assay-field-display-" + f.fieldName}>
              <td className="fw-bolder">{f.displayName}</td>
              <td>{!!value ? "True" : "False"}</td>
            </tr>
        );
      } else if (f.type === "DATE") {
        fields.push(
            <tr key={"assay-field-display-" + f.fieldName}>
              <td className="fw-bolder">{f.displayName}</td>
              <td>{!!value ? new Date(value).toLocaleString() : 'n/a'}</td>
            </tr>
        );
      } else if (f.type === "FILE") {
        let val = "n/a";
        if (value) {
          const d = JSON.parse(value);
          let url = d.url;
          // if (d.downloadable) {
          //   url = "/api/internal/data-files/download?locationId="; // TODO: fix reference to locationId
          // }
          val = <Button variant={"link"} href={url} target={"_blank"}>
            <FontAwesomeIcon icon={faFile} className={"me-2"} /> {d.name}
          </Button>
        }
        fields.push(
            <tr key={"assay-field-display-" + f.fieldName}>
              <td className="fw-bolder">{f.displayName}</td>
              <td>{val}</td>
            </tr>
        );
      }

    }
  }

  return (
      <Row>
        <Col xs={12}>
          <Table size={"sm"} borderless={true} striped={true}>
            <tbody>
              {fields}
            </tbody>
          </Table>
        </Col>
      </Row>
  )

};

AssayFieldData.propTypes = {
  assay: PropTypes.object.isRequired,
}

export default AssayFieldData;