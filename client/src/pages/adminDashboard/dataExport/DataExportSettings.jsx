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
import { Button, Card, Col, Row } from "react-bootstrap";
import { DismissableAlert } from "@/common/errors";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFileExport } from "@fortawesome/free-solid-svg-icons";
import { useMutation } from "@tanstack/react-query";

const DataExportSettings = () => {

  const [response, setResponse] = React.useState(null);

  const exportMutation = useMutation({
    mutationFn: () => axios.post("/api/internal/export", {}, {
      headers: {
        "Content-Type": "application/json",
      },
    }),
    onSuccess: (response) => {
      console.debug("Export response", response);
      setResponse(response.data);
    },
    onError: (e) => {
      console.error("Export error", e);
    },
  });

  return (
    <>

      <Card>
        <Card.Header>
          <Card.Title tag="h5" className="mb-0">
            Data Export
          </Card.Title>
        </Card.Header>
        <Card.Body>

          <Row>
            <Col>
              <div className="info-alert">
                Use this tool to export the contents of your Study Tracker database to a collection of CSV files.
                This archive can be used for backing-up your data or for migrating to{" "}
                <a href="https://labatlas.com" target="_blank" rel="noopener noreferrer">Lab Atlas</a>.
              </div>
            </Col>
          </Row>

          <Row>
            <Col>
              { exportMutation.isSuccess ? (
                <DismissableAlert
                  message={response.message || "Export started successfully. You can find the exported data in your application's temporary storage directory once complete."}
                  color="success"
                  dismissable={false}
                />
              ) : exportMutation.isError ? (
                <DismissableAlert
                  message={"There was a problem starting the export: " + exportMutation.error.message}
                  color="warning"
                  dismissable={false}
                />
              ) : (
                <div className="info-alert">
                  <h5>Export Data</h5>
                  <p>
                    Click the button below to start the export process. Once the export has completed, a ZIP file with the contents will be downloaded. Do not close this window after the export begins.
                  </p>
                  <div className={"text-center"}>
                    <Button
                      variant={"primary"}
                      size={"lg"}
                      onClick={() => window.open("/api/internal/export/sync")}
                      disabled={exportMutation.isPending}
                    >
                      <FontAwesomeIcon icon={faFileExport} className={"me-2"} />
                      { exportMutation.isPending ? "Starting..." : "Start Export" }
                    </Button>
                  </div>
                </div>
              )}
            </Col>
          </Row>

        </Card.Body>
      </Card>

    </>
  );

}

export default DataExportSettings;
