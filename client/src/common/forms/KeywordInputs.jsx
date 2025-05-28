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

import React, {useContext} from "react";
import {Badge, Col, Form, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import AsyncCreatable from "react-select/async-creatable";
import {FormGroup} from "./common";
import axios from "axios";
import PropTypes from "prop-types";
import NotyfContext from "../../context/NotyfContext";
import {faXmark} from "@fortawesome/free-solid-svg-icons";

const KeywordInputs = ({keywords, onChange}) => {

  const notyf = useContext(NotyfContext);

  const handleRemoveKeyword = (keyword) => {
    console.debug("Removing keyword", keyword);
    onChange(keywords.filter(k => k.id !== keyword.id));
  }

  const handleKeywordSelect = (selected) => {
    console.debug(selected);
    if (selected.__isNew__) {

      let category = null;
      let keyword = selected.label;
      if (selected.label.includes("::")) {
        const parts = selected.label.split("::");
        category = parts[0].trim();
        keyword = parts[1].trim();
      }

      axios.post("/api/internal/keyword", {
          keyword: keyword,
          category: category
        })
      .then(response => {
        onChange([...keywords, response.data])
      })
      .catch(e => {
        console.error(e);
        notyf.error("Failed to add new keyword");
      })
    } else {
      onChange([...keywords, selected])
    }
  }

  const keywordAutocomplete = (input) => {

    let category = null;
    let val = input;
    if (input.includes("::")) {
      const parts = input.split("::");
      category = parts[0].trim();
      val = parts[1].trim();
    }

    return axios.get(`/api/internal/keyword?q=${val}${category ? "&category=" + category : ''}`)
    .then(response => {
      return response.data.map(k => {
        return {
          id: k.id,
          label: (k.category ? k.category + " :: " : "" ) + k.keyword,
          // label: k.category.name + ": " + k.keyword,
          value: k.id,
          category: k.category,
          keyword: k.keyword
        }
      });
    }).catch(e => {
      console.error(e);
    })
  }

  return (
      <Row>
        <Col sm={7}>
          <FormGroup>
            <Form.Label>Keyword Search</Form.Label>
            <AsyncCreatable
                placeholder={"Search-for and select keywords..."}
                className={"react-select-container"}
                classNamePrefix={"react-select"}
                loadOptions={keywordAutocomplete}
                onChange={handleKeywordSelect}
                controlShouldRenderValue={false}
                createOptionPosition={"first"}
                // defaultOptions={true}
            />
            <Form.Text>
              Keywords are optional. When creating new keywords, you can use two colons
              (<code>::</code>) to separate the category from the keyword. For example,
              <code>Tissue::Liver</code> will create a keyword with the category
              <code>Tissue</code> and the keyword <code>Liver</code>.
            </Form.Text>
          </FormGroup>

          {
            keywords.map(k => {
              return (
                  <span className={"h4 me-2"} key={"keyword-" + k.id}>
                    <Badge bg={"primary"} className={"pt-2 pb-2 ps-3 pe-3"} pill>
                      {
                        k.category && <span className={"fw-light"}>{k.category}: </span>
                      }
                      <span className={"fw-bolder"}>{k.keyword}</span>
                      <FontAwesomeIcon
                          style={{cursor: "pointer"}}
                          icon={faXmark}
                          className={"ms-3"}
                          onClick={() => handleRemoveKeyword(k)}
                      />
                    </Badge>
                  </span>
              )
            })
          }
        </Col>
      </Row>
  );

}

KeywordInputs.propTypes = {
  keywords: PropTypes.array,
  onChange: PropTypes.func.isRequired
}

export default KeywordInputs;