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

import {
  Breadcrumb,
  Button,
  Card,
  Col,
  Container,
  Dropdown,
  Row
} from "react-bootstrap";
import React, {useContext} from "react";
import {Edit2, Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";
import {StatusBadge} from "../../common/status";
import PropTypes from "prop-types";
import StudyUpdateModal from "./StudyUpdateModal";
import swal from "sweetalert2";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../common/DataTable";

const StudyCollectionDetailsHeader = ({collection}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>{collection.name}</h3>
        </Col>
      </Row>
  );
};

StudyCollectionDetailsHeader.propTypes = {
  collection: PropTypes.object.isRequired,
}

const StudyCollectionDetails = ({
  collection,
  handleRemoveStudy,
  handleUpdateCollection,
  user
}) => {

  const [modalIsOpen, setModalIsOpen] = React.useState(false);
  const navigate = useNavigate();
  const notyf = useContext(NotyfContext);

  const handleStudiesUpdate = (studies) => {
    handleUpdateCollection({
      ...collection,
      studies: studies
    });
  };

  const handleCollectionDelete = () => {
    swal.fire({
      title: "Are you sure you want to delete this collection?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val.isConfirmed) {
        axios.delete("/api/internal/studycollection/" + collection.id)
        .then(() => {
          navigate("/collections");
        })
        .catch(error => {
          console.error(error);
          notyf.open({
            type: "error",
            message: "Error deleting collection."
          })
        })
      }
    });
  }

  const columnHelper = createColumnHelper();
  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "code",
      header: "Code",
      cell: (d) => {
        return (
            <a href={"/study/" + d.code}>
              {d.code}
            </a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.code.localeCompare(b.original.code);
      },
    }),
    columnHelper.accessor(row => row, {
      id: "status",
      header: "Status",
      sortingFn: (a, b) => {
        return a.original.status.label.localeCompare(b.original.status.label);
      },
      cell: (d) => <StatusBadge status={d.status}/>
    }),
    {
      id: "updatedAt",
      header: "Last Updated",
      accessorFn: (d) => new Date(d.updatedAt).toLocaleDateString()
    },
    {
      id: "program",
      header: "Program",
      sortingFn: (a, b) => {
        return a.original.program.name.localeCompare(b.original.program.name);
      },
      accessorFn: (d) => d.program.name
    },
    {
      id: "name",
      header: "Name",
      accessorFn: (d) => d.name
    },
    {
      id: "owner",
      header: "Owner",
      accessorFn: (d) => d.owner.displayName
    },
    columnHelper.accessor(row => row, {
      id: "cro",
      header: "CRO / Collaborator",
      sortingFn: (a, b) => {
        const da = a.original.collaborator ? a.original.collaborator.organizationName
            : '';
        const db = b.original.collaborator ? b.original.collaborator.organizationName
            : '';
        return da.localeCompare(db);
      },
      cell: (d) => d.collaborator && (
            <div>
              <p style={{fontWeight: 'bold', marginBottom: '0.2rem'}}>
                {d.collaborator.organizationName}
              </p>
              <p>
                {d.externalCode}
              </p>
            </div>

        )
    }),
  ], []);

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/collections"}>
                Collections
              </Breadcrumb.Item>
              <Breadcrumb.Item active>
                Collection Detail
              </Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <StudyCollectionDetailsHeader collection={collection} user={user}/>

        <Row>

          <Col xs={12} md={6}>
            <Card className="details-card">

              <Card.Header>
                <div className="card-actions float-end">
                  <Dropdown align="end">
                    <Dropdown.Toggle as="a" bsPrefix="-">
                      <Menu/>
                    </Dropdown.Toggle>
                    <Dropdown.Menu>

                      <Dropdown.Item
                          href={"/collection/" + collection.id + "/edit"}>
                        <FontAwesomeIcon className={"me-2"} icon={faEdit}/>
                        Edit
                      </Dropdown.Item>

                      <Dropdown.Item onClick={handleCollectionDelete}>
                        <FontAwesomeIcon className={"me-2"} icon={faTrash}/>
                        Delete
                      </Dropdown.Item>

                    </Dropdown.Menu>
                  </Dropdown>
                </div>
                <Card.Title tag="h5" className="mb-0 text-muted">
                  Summary
                </Card.Title>
              </Card.Header>

              <Card.Body>

                <Row>

                  <Col xs={12}>
                    <h6 className="details-label">Description</h6>
                    <p>{collection.name}</p>
                  </Col>

                </Row>

                <Row>

                  <Col xs={6} sm={4}>
                    <h6 className="details-label">Created By</h6>
                    <p>{collection.createdBy.displayName}</p>
                  </Col>

                  <Col xs={6} sm={4}>
                    <h6 className="details-label">Last Modified By</h6>
                    <p>{collection.lastModifiedBy.displayName}</p>
                  </Col>

                </Row>

                <Row>

                  <Col xs={6} sm={4}>
                    <h6 className="details-label">Date Created</h6>
                    <p>{new Date(collection.createdAt).toLocaleString()}</p>
                  </Col>

                  <Col xs={6} sm={4}>
                    <h6 className="details-label">Last Updated</h6>
                    <p>{new Date(collection.updatedAt).toLocaleString()}</p>
                  </Col>

                </Row>

              </Card.Body>

            </Card>
          </Col>

          <Col xs={12}>
            <Card className="details-card">

              <Card.Header>
                <Card.Title tag="h5" className="mb-0 text-muted">
                  Studies
                </Card.Title>
              </Card.Header>

              <Card.Body>
                <Container fluid className="animated fadeIn">

                  <Row>
                    <Col lg={12}>
                      <div className={"d-flex justify-content-between"}>
                        <div>
                          <Button variant={"primary"} onClick={() => setModalIsOpen(true)}>
                            <Edit2 className="feather align-middle me-2" />
                            Edit Studies
                          </Button>
                        </div>
                      </div>
                      <DataTable data={collection.studies} columns={columns} />
                    </Col>
                  </Row>

                </Container>
              </Card.Body>

            </Card>
          </Col>

        </Row>

        <StudyUpdateModal
            isOpen={modalIsOpen}
            closeModal={() => setModalIsOpen(false)}
            studies={collection.studies}
            handleUpdate={handleStudiesUpdate}
        />

      </Container>
  );

}

StudyCollectionDetails.propTypes = {
  collection: PropTypes.object.isRequired,
  user: PropTypes.object.isRequired,
  handleUpdateCollection: PropTypes.func.isRequired,
  handleRemoveStudy: PropTypes.func.isRequired,
}

export default StudyCollectionDetails;