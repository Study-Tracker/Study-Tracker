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
import PropTypes from "prop-types";
import {Card, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faHardDrive} from "@fortawesome/free-regular-svg-icons";
import {DriveStatusBadge} from "../../../common/fileManager/folderBadges";
import {faCancel, faGears} from "@fortawesome/free-solid-svg-icons";

const EgnyteDriveCard = ({drive, handleDriveStatusChange}) => {
 return (
     <Card className={"mt-3"}>
       <Card.Body>
         <Row>

           <Col xs={1} className={"d-flex align-items-center"}>
             <FontAwesomeIcon icon={faHardDrive} size={"2x"} className={"text-secondary"}/>
           </Col>

           <Col xs={7} className={"d-flex align-items-center"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  {drive.storageDrive.displayName}
                </span>
                <br/>
                 <span className={"text-muted"}>
                  {drive.storageDrive.rootPath}
                </span>
              </div>
           </Col>

           <Col xs={2} className={"d-flex align-items-center"}>
             <div>
               <span className="text-muted">Status</span>
               <br />
               <DriveStatusBadge active={drive.storageDrive.active} />
             </div>
           </Col>

           <Col xs={2} className={"d-flex align-items-center"}>
             <Dropdown>
               <Dropdown.Toggle variant="outline-primary">
                 <FontAwesomeIcon icon={faGears} className={"me-1"} />
               </Dropdown.Toggle>
               <Dropdown.Menu>

                 {
                     drive.storageDrive.active ? (
                         <Dropdown.Item onClick={() => handleDriveStatusChange(drive.id, false)}>
                           <FontAwesomeIcon icon={faCancel} className={"me-1"} />
                           Set Inactive
                         </Dropdown.Item>
                     ) : (
                         <Dropdown.Item onClick={() => handleDriveStatusChange(drive.id, true)}>
                           <FontAwesomeIcon icon={faCheckCircle} className={"me-1"} />
                           Set Active
                         </Dropdown.Item>
                     )
                 }

               </Dropdown.Menu>
             </Dropdown>
           </Col>

         </Row>
       </Card.Body>
     </Card>
 )
}

EgnyteDriveCard.propTypes = {
  drive: PropTypes.object.isRequired
}

export default EgnyteDriveCard;

