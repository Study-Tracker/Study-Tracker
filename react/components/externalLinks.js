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

import {Alert, Button, Card, Col, Form, Modal, Row} from "react-bootstrap";
import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLink, faTimesCircle} from "@fortawesome/free-solid-svg-icons";
import swal from 'sweetalert';
import {PlusCircle} from "react-feather";

class ExternalLinks extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      links: props.links,
      modalIsOpen: false,
      newLink: {
        label: '',
        url: ''
      },
      modalError: null
    };
    this.showModal = this.showModal.bind(this);
    this.handleNewLinkChange = this.handleNewLinkChange.bind(this);
    this.handleNewLinkSubmit = this.handleNewLinkSubmit.bind(this);
    this.handleLinkDelete = this.handleLinkDelete.bind(this);
  }

  showModal(bool) {
    this.setState({
      modalIsOpen: bool
    })
  }

  handleNewLinkChange(props) {
    this.setState({
      newLink: {
        ...this.state.newLink,
        ...props
      }
    })
  }

  handleNewLinkSubmit() {
    let l = this.state.newLink;
    if (!l.label || !l.url) {
      this.setState({
        modalError: "One or more required fields are missing. Please check your inputs and then try again."
      });
      return;
    }
    fetch("/api/study/" + this.props.studyCode + "/links", {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(l)
    }).then(response => {
      if (response.ok) {
        this.setState({
          links: [...this.state.links, l],
          newLink: {
            label: '',
            url: ''
          },
          modalError: null
        });
      } else {
        throw new Error("Failed to add external link");
      }
      this.showModal(false);
    })
    .catch(error => {
      console.error(error);
      this.setState({
        modalError: "Failed to create link. Please check your inputs and try again."
      });
    })

  }

  handleLinkDelete(link) {
    swal({
      title: "Are you sure you want to delete this link?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        fetch("/api/study/" + this.props.studyCode + "/links/" + link.id, {
          method: 'DELETE',
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(link)
        }).then(response => {
          this.setState({
            links: this.state.links.filter(
                l => l.label !== link.label && l.url !== link.url)
          });
        })
        .catch(error => {
          console.error(error);
          this.setState({
            modalError: "Failed to create link. Please check your inputs and try again."
          });
        })
      }
    });
  }

  render() {

    const links = this.state.links.map(link => {
      return (
          <li key={"external-link-" + link.id}>
            <FontAwesomeIcon icon={faLink}/>
            &nbsp;&nbsp;
            <a href={link.url} target="_blank">{link.label}</a>
            &nbsp;&nbsp;&nbsp;&nbsp;
            {
              !!this.props.user ? (
                  <a onClick={() => this.handleLinkDelete(link)}>
                    <FontAwesomeIcon color={"red"} icon={faTimesCircle}/>
                  </a>
              ) : ''
            }
          </li>
      );
    });

    return (
        <div>

          <Card.Title>
            External Links
            {
              !!this.props.user ? (
                  <span className="float-end">
                  <Button size={"sm"} variant={"primary"}
                          onClick={() => this.showModal(true)}>
                    Add <PlusCircle className="feather feather-button-sm"/>
                  </Button>
                </span>
              ) : ''
            }
          </Card.Title>

          {
            links.length
                ? (
                    <ul className="list-unstyled">
                      {links}
                    </ul>
                )
                : (
                    <p className="text-muted text-center">
                      No external links.
                    </p>
                )
          }

          <Modal
              show={this.state.modalIsOpen}
              onHide={() => this.showModal(false)}
          >

            <Modal.Header closeButton>
              Add New External Link
            </Modal.Header>

            <Modal.Body className="m-3">

              <Row>

                <Col sm={12}>
                  <p>
                    Please provide a complete URL to the target resource, as
                    well as a descriptive label.
                  </p>
                </Col>

                <Col sm={12}>
                  <Form.Group>
                    <Form.Label>Label *</Form.Label>
                    <Form.Control
                        type="text"
                        defaultValue={this.state.newLink.label}
                        onChange={(e) => this.handleNewLinkChange({
                          label: e.target.value
                        })}
                    />
                  </Form.Group>
                </Col>

                <Col sm={12}>
                  <Form.Group>
                    <Form.Label>URL *</Form.Label>
                    <Form.Control
                        type="text"
                        defaultValue={this.state.newLink.url}
                        onChange={(e) => this.handleNewLinkChange({
                          url: e.target.value
                        })}
                    />
                  </Form.Group>
                </Col>

              </Row>
              {
                !!this.state.modalError
                    ? (
                        <Row>
                          <Col sm={12}>
                            <Alert variant={"warning"}>
                              <div className="alert-message">
                                {this.state.modalError}
                              </div>
                            </Alert>
                          </Col>
                        </Row>
                    ) : ''
              }

            </Modal.Body>

            <Modal.Footer>
              <Button variant={"secondary"} onClick={() => this.showModal(false)}>
                Cancel
              </Button>
              <Button variant={"primary"}
                      onClick={this.handleNewLinkSubmit}>
                Save
              </Button>
            </Modal.Footer>

          </Modal>

        </div>
    );
  }

}

export default ExternalLinks;