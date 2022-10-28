/*
 * Copyright 2022 the original author or authors.
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

import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserCircle} from "@fortawesome/free-solid-svg-icons";
import {KeywordCategoryBadge} from "./keywords";
import {Button, Card, Modal} from "react-bootstrap";
import {PlusCircle} from "react-feather";
import KeywordInputs from "./forms/KeywordInputs";
import swal from "sweetalert";
import PropTypes from "prop-types";
import axios from "axios";

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

StudyTeam.propTypes = {
  users: PropTypes.array.isRequired,
  owner: PropTypes.object.isRequired
}

export const StudyKeywords = props => {

  const [keywords, setKeywords] = useState(props.keywords || []);
  const [previousKeywords, setPreviousKeywords] = useState(props.keywords || []);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [keywordCategories, setKeywordCategories] = useState(null);

  const showModal = async (bool) => {
    if (bool && !keywordCategories) {
      await axios.get("/api/internal/keyword-category")
        .then(res => setKeywordCategories(res.data));
    }
    setModalIsOpen(bool);
  }

  const handleKeywordsSave = () => {
    axios.put("/api/internal/study/" + props.studyId + "/keywords", keywords)
    .then(res => {
      setPreviousKeywords(keywords);
      setModalIsOpen(false);
      swal("Study keywords updated successfully", "", "success");
    })
    .catch(e => {
      console.error(e);
      swal("Keyword update failed", "Please try again. If the error persists, "
          + "contact the system administrator.", "warning");
    });
  }

  const handleKeywordsCancel = () => {
    setKeywords(previousKeywords);
    setModalIsOpen(false);
  }

  const links = keywords.map(keyword => {
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
                    onClick={() => showModal(true)}>
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
            show={modalIsOpen}
            onHide={() => showModal(false)}
            size={'lg'}
        >

          <Modal.Header closeButton>
            Add Keywords
          </Modal.Header>

          <Modal.Body className="m-3">
            <KeywordInputs
                keywords={keywords}
                keywordCategories={keywordCategories}
                onChange={(data) => setKeywords(data)}
            />
          </Modal.Body>

          <Modal.Footer>
            <Button variant={"secondary"}
                    onClick={handleKeywordsCancel}>
              Cancel
            </Button>
            <Button variant={"primary"}
                    onClick={handleKeywordsSave}>
              Save
            </Button>
          </Modal.Footer>

        </Modal>

      </React.Fragment>
  );

}

StudyKeywords.propTypes = {
  keywords: PropTypes.array.isRequired
}