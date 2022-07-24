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

import React, {useState} from "react";
import {Col, Form, Row} from "react-bootstrap";
import Select from "react-select";
import {KeywordCategoryBadge} from "../keywords";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import AsyncCreatable from "react-select/async-creatable";
import swal from "sweetalert";
import {FormGroup} from "./common";
import axios from "axios";
import PropTypes from "prop-types";

const KeywordInputs = ({keywords, keywordCategories, onChange}) => {

  const [selectedCategory, setSelectedCategory] = useState(null);
  const categoryOptions = keywordCategories.map(c => {
    return {label: c.name, value: c.id}
  });

  const handleCategoryChange = (selected) => {
    console.debug(selected);
    setSelectedCategory(keywordCategories.find(c => c.id === selected.value))
  }

  const handleRemoveKeyword = (e) => {
    const selected = parseInt(e.currentTarget.dataset.id);
    onChange(keywords.filter(k => k.id !== selected));
  }

  const handleKeywordSelect = (selected) => {
    console.debug(selected);
    if (selected.__isNew__) {
      axios.post("/api/keyword", {
          keyword: selected.label,
          category: selectedCategory
        })
      .then(response => {
        onChange([
          ...keywords,
          response.data
        ])
      })
      .catch(e => {
        console.error(e);
        swal("Failed to add new keyword",
            "Please try again. If the problem persists, contact Study Tracker support for help.",
            "warning");
      })
    } else {
      onChange([
        ...keywords,
        selected
      ])
    }
  }

  const keywordAutocomplete = (input, callback) => {
    axios.get('/api/keyword/?q=' + input
        + (!!selectedCategory ? "&categoryId=" + selectedCategory.id : ''))
    .then(response => {
      const kw = response.data.map(k => {
        return {
          id: k.id,
          label: k.keyword,
          // label: k.category.name + ": " + k.keyword,
          value: k.id,
          category: k.category,
          keyword: k.keyword
        }
      });
      callback(kw);
    }).catch(e => {
      console.error(e);
    })
  }

  const selectedKeywords = keywords.map(keyword => {
    console.debug(keyword);
    return (
        <Row
            key={"keyword-" + keyword.id}
            className="align-items-center justify-content-center mt-1"
        >
          <Col xs={3}>
            <KeywordCategoryBadge label={keyword.category.name}/>
          </Col>
          <Col xs={7}>
            {keyword.keyword}
          </Col>
          <Col xs={2}>
            <a onClick={handleRemoveKeyword}
               data-id={keyword.id}>
              <FontAwesomeIcon
                  icon={faTimesCircle}
                  className="align-middle me-2 text-danger"
              />
            </a>
          </Col>
        </Row>
    )
  });

  return (
      <Row>
        <Col sm={2}>
          <FormGroup>
            <Form.Label>Category</Form.Label>
            <Select
                className="react-select-container"
                classNamePrefix="react-select"
                options={categoryOptions}
                onChange={handleCategoryChange}
            />
          </FormGroup>
        </Col>
        <Col sm={5}>
          <FormGroup>
            <Form.Label>Keyword Search</Form.Label>
            <AsyncCreatable
                placeholder={"Search-for and select keywords..."}
                className={"react-select-container"}
                classNamePrefix={"react-select"}
                loadOptions={keywordAutocomplete}
                onChange={handleKeywordSelect}
                controlShouldRenderValue={false}
                isDisabled={!selectedCategory}
                createOptionPosition={"first"}
                // defaultOptions={true}
            />
          </FormGroup>
        </Col>
        <Col sm={5}>
          <Row>
            <Col xs={12}>
              <Form.Label>Selected</Form.Label>
            </Col>
          </Row>
          {selectedKeywords}
        </Col>
      </Row>
  );

}

KeywordInputs.propTypes = {
  keywords: PropTypes.array,
  keywordCategories: PropTypes.array,
  onChange: PropTypes.func.isRequired
}

export default KeywordInputs;