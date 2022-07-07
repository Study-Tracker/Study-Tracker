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

import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserCircle} from "@fortawesome/free-solid-svg-icons";
import {KeywordCategoryBadge} from "./keywords";
import {Button, Card, Modal} from "react-bootstrap";
import {PlusCircle} from "react-feather";
import {KeywordInputs} from "./forms/keywords";
import {getCsrfToken} from "../config/csrf";
import swal from "sweetalert";

export const StudyTeam = ({users, owner}) => {
  const list = users.map(user => {
    if (user.id === owner.id) {
      return (
          <li key={new Date()}>
            <a href={"mailto:" + user.email} className={"text-info"}>
              <FontAwesomeIcon icon={faUserCircle}/>
              &nbsp;
              {user.displayName} (owner)
            </a>
          </li>);
    } else {
      return (
          <li key={"study-user-" + user.id}>
            <FontAwesomeIcon icon={faUserCircle}/>
            &nbsp;
            {user.displayName}
          </li>
      );
    }
  });
  return (
      <ul className="list-unstyled">
        {list}
      </ul>
  );
};

export class StudyKeywords extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      keywords: props.keywords,
      previousKeywords: props.keywords,
      modalIsOpen: false
    }
    this.handleKeywordsUpdate = this.handleKeywordsUpdate.bind(this);
    this.handleKeywordsSave = this.handleKeywordsSave.bind(this);
    this.handleKeywordsCancel = this.handleKeywordsCancel.bind(this);
  }

  async showModal(bool) {
    let keywordCategories = this.state.keywordCategories;
    if (bool && !keywordCategories) {
      keywordCategories = await fetch("/api/keyword-category").then(res => res.json());
    }
    this.setState({
      modalIsOpen: bool,
      keywordCategories: keywordCategories
    })
  }

  handleKeywordsUpdate(data) {
    console.log(data);
    this.setState({keywords: data.keywords});
  }

  handleKeywordsSave() {
    fetch("/api/study/" + this.props.studyId + "/keywords", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": getCsrfToken()
      },
      body: JSON.stringify(this.state.keywords)
    })
    .then(res => {
      if (res.ok) {
        swal("Study keywords updated successfully", "", "success");
        this.setState({
          previousKeywords: this.state.keywords,
          modalIsOpen: false
        });
      } else {
        throw new Error("Failed to update study keywords");
      }
    })
    .catch(e => {
      console.error(e);
      swal("Keyword update failed", "Please try again. If the error persists, "
          + "contact the system administrator.", "warning");
    })
  }

  handleKeywordsCancel() {
    this.setState({
      keywords: this.state.previousKeywords,
      modalIsOpen: false
    })
  }

  render() {

    const links = this.state.keywords.map(keyword => {
      return (
          <li key={"keyword-" + keyword.id} className="mt-1">
            <KeywordCategoryBadge label={keyword.category.name}/>
            &nbsp;&nbsp;
            {keyword.keyword}
          </li>
      );
    });

    return (
        <React.Fragment>

          <Card.Title>
            Keywords
            <span className="float-end">
              <Button size={"sm"} variant={"primary"}
                      onClick={() => this.showModal(true)}>
                Add <PlusCircle className="feather feather-button-sm"/>
              </Button>
            </span>
          </Card.Title>

          {
            links.length
                ? (
                    <ul className="list-unstyled">
                      {links}
                    </ul>
                ) : (
                    <p className="text-muted text-center">
                      No keywords.
                    </p>
                )
          }

          <Modal
              show={this.state.modalIsOpen}
              onHide={() => this.showModal(false)}
              size={'lg'}
          >

            <Modal.Header closeButton>
              Add Keywords
            </Modal.Header>

            <Modal.Body className="m-3">
              <KeywordInputs
                  keywords={this.state.keywords}
                  keywordCategories={this.state.keywordCategories}
                  onChange={this.handleKeywordsUpdate}
              />
            </Modal.Body>

            <Modal.Footer>
              <Button variant={"secondary"}
                      onClick={this.handleKeywordsCancel}>
                Cancel
              </Button>
              <Button variant={"primary"}
                      onClick={this.handleKeywordsSave}>
                Save
              </Button>
            </Modal.Footer>

          </Modal>

        </React.Fragment>
    );
  }

}

export const StudyCollaborator = ({collaborator, externalCode}) => {
  return (
      <div className="collaborator mb-4">
        <table>
          <tbody>
          <tr>
            <td><strong>External Study Code:</strong></td>
            <td><strong>{externalCode}</strong></td>
          </tr>
          <tr>
            <td><strong>Label: </strong></td>
            <td>{collaborator.label}</td>
          </tr>
          <tr>
            <td><strong>Organization Name: </strong></td>
            <td>{collaborator.organizationName}</td>
          </tr>
          <tr>
            <td><strong>Organization Location: </strong></td>
            <td>{collaborator.organizationLocation}</td>
          </tr>
          <tr>
            <td><strong>Contact Person: </strong></td>
            <td>{collaborator.contactPersonName}</td>
          </tr>
          <tr>
            <td><strong>Contact Email: </strong></td>
            <td>
              <a href={"mailto:" + collaborator.contactEmail}>
                {collaborator.contactEmail}
              </a>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
  );
};