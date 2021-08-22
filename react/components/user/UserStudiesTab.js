/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Row} from "reactstrap";
import React from "react";
import {StudySummaryCards} from "../studies";

const UserStudiesTab = ({studies, user}) => {

  return (
      <div>
        <Row className="justify-content-between align-items-center">
          <div className="col-6">
            <h4>User Studies</h4>
          </div>
          {/*<div className="col-auto">*/}
          {/*  {*/}
          {/*    !!user*/}
          {/*        ? (*/}
          {/*            <Button color="info"*/}
          {/*                    onClick={() => history.push("/studies/new")}>*/}
          {/*              New Study*/}
          {/*              &nbsp;*/}
          {/*              <FontAwesomeIcon icon={faPlusCircle}/>*/}
          {/*            </Button>*/}
          {/*        ) : ''*/}
          {/*  }*/}
          {/*</div>*/}
        </Row>

        <StudySummaryCards studies={studies} showDetails={true}/>
      </div>
  );

};

export default UserStudiesTab;