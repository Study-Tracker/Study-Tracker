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

import React, {useContext, useEffect, useState} from "react";
import {Col, Form, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import FeatureToggleCard from "./FeatureToggleCard";
import axios from "axios";
import {S3_STORAGE_TYPE} from "../../config/storageConstants";
import {FormGroup} from "./common";
import Select from "react-select";
import NotyfContext from "../../context/NotyfContext";
import {DismissableAlert} from "../errors";

const S3InputsCard = ({
    isActive,
    onChange,
    selectedProgram,
    errors
}) => {

  console.debug("Program: ", selectedProgram);
  const [enabled, setEnabled] = useState(false);
  const [rootFolders, setRootFolders] = useState([]);
  const [selectedBucket, setSelectedBucket] = useState(null);
  const notyf = useContext(NotyfContext);

  useEffect(() => {
    axios.get("/api/internal/integrations/aws/")
    .then(r => {
      console.debug("AWS Integration: ", r.data);
      if (r.data.length && r.data[0].active) {
        axios.get("/api/internal/storage-drive-folders?studyRoot=true")
        .then(response => {
          let folders = response.data;
          axios.get("/api/internal/drives/s3")
          .then(async response2 => {
            const bucketRootFolders = folders
            .filter(f => f.storageDrive.active && f.storageDrive.driveType
                === S3_STORAGE_TYPE);
            await bucketRootFolders.forEach(f => {
              f.bucket = response2.data.find(b => b.storageDrive.id === f.storageDrive.id);
            });
            setEnabled(true);
            setRootFolders(bucketRootFolders);
          })
        })
      } else {
        setEnabled(false);
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Error loading S3 buckets"
      })
    })
  }, [notyf]);

  console.debug("Selected Bucket", selectedBucket);

  return (
      <>
      {
        enabled && (
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

                {
                  rootFolders.length ? (
                      <Col md={6} className={"mb-3"}>
                        <FormGroup>
                          <Form.Label>S3 Bucket *</Form.Label>
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
                                setSelectedBucket(selected.value);
                                onChange("s3FolderId", selected.value.id);
                              }}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {errors.s3FolderId}
                          </Form.Control.Feedback>
                          <Form.Text>
                            Select the S3 bucket to create the study storage folder in.
                          </Form.Text>
                        </FormGroup>
                      </Col>
                  ) : (
                      <Col>
                        <DismissableAlert
                            header={"No S3 buckets available."}
                            message={"Please contact your administrator to enable S3 storage for studies."}
                            color={"warning"}
                        />
                      </Col>
                  )
                }

                {
                    selectedBucket && (
                        <Col md={6}>
                          <p>
                            A folder will be created for your study in the S3 bucket:
                          </p>
                          <p className={"text-lg"}>
                            <code>
                              {
                                  "s3://"
                                  + selectedBucket.bucket.name
                                  + "/"
                                  + selectedBucket.path
                                  + (selectedProgram ? selectedProgram.name : "")
                              }
                            </code>
                          </p>
                        </Col>
                    )
                }

              </Row>

            </FeatureToggleCard>
          )
      }
      </>
  );
}

S3InputsCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string
}

export default S3InputsCard;