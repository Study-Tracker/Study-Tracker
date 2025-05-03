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

import React from "react";
import {Button, Card} from "react-bootstrap";
import {PlusCircle} from "react-feather";
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import axios from "axios";
import AssayTypeTable from "./AssayTypeTable";
import {useQuery} from "@tanstack/react-query";

const AssayTypeSettings = () => {

  const {data: assayTypes, isLoading, error} = useQuery("assayTypes", () => {
    return axios.get("/api/internal/assaytype")
    .then(response => response.data);
  });

  if (isLoading) return <SettingsLoadingMessage/>;
  if (error) return <SettingsErrorMessage/>;

  return (
      <React.Fragment>
        <Card>

          <Card.Header>
            <Card.Title tag="h5" className="mb-0">
              Assay Types
              <span className="float-end">
              <Button
                  variant={"primary"}
                  href={"/assaytypes/new"}
              >
                New Assay Type
                &nbsp;
                <PlusCircle className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
            </Card.Title>
          </Card.Header>

          <Card.Body>
            <AssayTypeTable assayTypes={assayTypes}/>
          </Card.Body>

        </Card>
      </React.Fragment>
  )

}

export default AssayTypeSettings;