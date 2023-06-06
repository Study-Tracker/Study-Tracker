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

import {Button, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faArrowDown19,
    faArrowDown91,
    faArrowDownAZ,
    faArrowDownZA,
    faPlusCircle
} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect} from "react";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import AssaySummaryCard from "./AssaySummaryCard";
import StudyAssaysContentPlaceholder from "./StudyAssaysContentPlaceholder";

const StudyAssaysTab = ({study}) => {

  const sortAtoZ = (a, b) => a.name.localeCompare(b.name);
  const sortZtoA = (a, b) => b.name.localeCompare(a.name);
  const sortNewestFirst = (a, b) => new Date(b.createdAt) - new Date(a.createdAt);
  const sortOldestFirst = (a, b) => new Date(a.createdAt) - new Date(b.createdAt);

  const navigate = useNavigate();
  const notyf = React.useContext(NotyfContext);
  const [assays, setAssays] = React.useState([]);
  // const [sortOrder, setSortOrder] = React.useState("NEWEST_FIRST");
  const [sortIcon, setSortIcon] = React.useState(faArrowDown19);

  useEffect(() => {
    axios.get("/api/internal/study/" + study.code + "/assays")
    .then(response => {
      console.debug("Assays", response.data);
      setAssays(response.data
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .filter(a => a.active));
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Error loading assays."
      })
    })
  }, [notyf]);

  const handleAssaySort = (order) => {
    let updated = Array.from(assays);
    switch (order) {
      case "A_TO_Z":
        setAssays(updated.sort(sortAtoZ));
        setSortIcon(faArrowDownAZ);
        return;
      case "Z_TO_A":
        setAssays(updated.sort(sortZtoA));
        setSortIcon(faArrowDownZA);
        return;
      case "NEWEST_FIRST":
        setAssays(updated.sort(sortNewestFirst));
        setSortIcon(faArrowDown19);
        return;
      case "OLDEST_FIRST":
        setAssays(updated.sort(sortOldestFirst));
        setSortIcon(faArrowDown91);
        return;
      default:
        setAssays(updated.sort(sortNewestFirst));
        setSortIcon(faArrowDown19);
        return;
    }
  }

  const handleAddAssay = () => {
    navigate("/study/" + study.code + "/assays/new");
  }

  return (
      <>

        <Row className="mb-3 justify-content-end">

          <div className="col-auto">
            <Dropdown className="me-1 mb-1">
              <Dropdown.Toggle variant={"outline-info"}>
                <FontAwesomeIcon icon={sortIcon} className={"me-2"}/>
                Sort
              </Dropdown.Toggle>
              <Dropdown.Menu>

                <Dropdown.Item onClick={() => handleAssaySort("NEWEST_FIRST")}>
                  Newest first
                </Dropdown.Item>

                <Dropdown.Item onClick={() => handleAssaySort("OLDEST_FIRST")}>
                  Oldest first
                </Dropdown.Item>

                <Dropdown.Item onClick={() => handleAssaySort("A_TO_Z")}>
                 A-Z
                </Dropdown.Item>

                <Dropdown.Item onClick={() => handleAssaySort("Z_TO_A")}>
                  Z-A
                </Dropdown.Item>

              </Dropdown.Menu>
            </Dropdown>
          </div>

          <div className="col-auto">
            <Button
                variant="info"
                onClick={handleAddAssay}
            >
              <FontAwesomeIcon icon={faPlusCircle} className="me-2"/>
              Add Assay
            </Button>
          </div>

        </Row>

        {
          assays && assays.length > 0 && (
            assays.map(assay => (
                <Row>
                  <Col xs={12}>
                    <AssaySummaryCard assay={assay} study={study}/>
                  </Col>
                </Row>
            ))
          )
        }

        {
          (!assays || assays.length === 0) && (
              <Row>
                <Col xs={12}>
                  <StudyAssaysContentPlaceholder handleClick={handleAddAssay}/>
                </Col>
              </Row>
          )
        }

      </>
  );

};

StudyAssaysTab.propTypes = {
  study: PropTypes.object.isRequired
}

export default StudyAssaysTab;