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

import React, {useEffect, useState} from "react";
import {Col, Form, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import FeatureToggleCard from "./FeatureToggleCard";
import axios from "axios";
import {S3_STORAGE_TYPE} from "../../config/storageConstants";
import {FormGroup} from "./common";
import Select from "react-select";

const S3InputsCard = ({
    isActive,
    onChange,
    selectedProgram
}) => {

  console.debug("Program: ", selectedProgram);
  const [rootFolders, setRootFolders] = useState([]);
  const [error, setError] = useState(null);
  const [selectedBucket, setSelectedBucket] = useState(null);

  useEffect(() => {
    axios.get("/api/internal/storage-drive-folders?studyRoot=true")
    .then(response => {
      const bucketRootFolders = response.data
        .filter(f => f.storageDrive.active && f.storageDrive.driveType === S3_STORAGE_TYPE);
      setRootFolders(bucketRootFolders);
    })
    .catch(error => {
      console.error(error);
      setError(error);
    })
  }, []);

  console.debug("Selected Bucket", selectedBucket);
  let bucketPath = selectedBucket ? selectedBucket.name + "/" + selectedBucket.rootFolderPath : "";
  if (!!bucketPath && !bucketPath.endsWith("/")) {
    bucketPath += "/";
  }
  if (selectedProgram) {
    bucketPath += selectedProgram.name;
  }

  return (
      <FeatureToggleCard
          isActive={isActive}
          title={"S3 Storage Folder"}
          description={"AWS S3 storage allows for the storage of large amounts "
              + "of data.  By enabling this feature for your study, a folder "
              + "will be created in S3 that can be used for uploading and "
              + "preserving large data files. All connected S3 buckets can be "
              + "accessed from the File Manager page."}
          switchLabel={"Does this study need S3 storage?"}
          handleToggle={() => onChange("useS3", !isActive)}
      >

        <Row>
          <Col md={12}>
            <p>
              A folder will be created for your study in the S3 bucket:
            </p>
            <p className={"text-lg"}>
              <code>{bucketPath}</code>
            </p>
          </Col>
        </Row>

        <Row>
          <Col md={6} className={"mb-3"}>
            <FormGroup>
              <Form.Label>Parent Folder *</Form.Label>
              <Select
                  className="react-select-container"
                  classNamePrefix="react-select"
                  options={
                    rootFolders
                    .sort((a, b) => a.name.localeCompare(b.name))
                    .map(p => ({
                      label: p.storageDrive.driveType + ": " + p.name,
                      value: p
                    }))
                  }
                  name="parentFolder"
                  onChange={(selected) => {
                    setFieldValue("parentFolder", selected.value);
                  }}
              />
              <Form.Control.Feedback type={"invalid"}>
                {errors.parentFolder}
              </Form.Control.Feedback>
              <Form.Text>
                Select the parent folder to create the study storage folder in.
              </Form.Text>
            </FormGroup>
          </Col>
        </Row>

      </FeatureToggleCard>
  );
}

S3InputsCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string
}

export default S3InputsCard;