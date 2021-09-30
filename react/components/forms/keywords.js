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

import React from 'react';
import {Col, FormGroup, Label, Row} from "reactstrap";
import Select from "react-select";
import {KeywordCategoryBadge} from "../keywords";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import AsyncCreatable from "react-select/async-creatable";
import swal from "sweetalert";

export default class KeywordInputs extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      categories: props.keywordCategories.map(c => {
        return {label: c, value: c}
      })
    };
    this.handleCategoryChange = this.handleCategoryChange.bind(this);
    this.handleKeywordSelect = this.handleKeywordSelect.bind(this);
    this.keywordAutocomplete = this.keywordAutocomplete.bind(this);
    this.handleRemoveKeyword = this.handleRemoveKeyword.bind(this);
  }

  handleCategoryChange(selected) {
    console.log(selected);
    this.setState({
      category: selected.value
    })
  }

  handleRemoveKeyword(e) {
    const selected = parseInt(e.currentTarget.dataset.id);
    const keywords = this.props.keywords.filter(k => k.id !== selected);
    this.props.onChange({
      keywords: keywords
    });
  }

  handleKeywordSelect(selected) {
    console.log(selected);
    if (!!selected.__isNew__) {
      fetch("/api/keyword", {
        method: 'POST',
        body: JSON.stringify({
          // id: null,
          // label: selected.label,
          // value: null,
          keyword: selected.label,
          category: this.state.category
        }),
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json"
        }
      })
      .then(response => response.json())
      .then(keyword => {
        this.props.onChange({
          keywords: [
            ...this.props.keywords,
            keyword
          ]
        })
      })
      .catch(e => {
        console.error(e);
        swal("Failed to add new keyword",
            "Please try again. If the problem persists, contact Study Tracker support for help.",
            "warning");
      })
    } else {
      this.props.onChange({
        keywords: [
          ...this.props.keywords,
          selected
        ]
      })
    }
  }

  keywordAutocomplete(input, callback) {
    if (input.length < 1) {
      return;
    }
    fetch('/api/keyword/?q=' + input
        + (!!this.state.category ? "&category=" + this.state.category : ''))
    .then(response => response.json())
    .then(json => {
      const keywords = json.map(k => {
        return {
          id: k.id,
          label: k.keyword,
          value: k.id,
          category: k.category,
          keyword: k.keyword
        }
      // }).sort((a, b) => {
      //   if (a.keyword > b.keyword) {
      //     return -1;
      //   } else if (a.keyword < b.keyword) {
      //     return 1;
      //   } else {
      //     return 0;
      //   }
      });
      callback(keywords);
    }).catch(e => {
      console.error(e);
    })
  }

  render() {

    const selectedKeywords = this.props.keywords.map(keyword => {
      console.log(keyword);
      return (
          <Row
              key={"keyword-" + keyword.id}
              className="align-items-center justify-content-center mt-1"
          >
            <Col xs="3">
              <KeywordCategoryBadge category={keyword.category}/>
            </Col>
            <Col xs="7">
              {keyword.keyword}
            </Col>
            <Col xs="2">
              <a onClick={this.handleRemoveKeyword}
                 data-id={keyword.id}>
                <FontAwesomeIcon
                    icon={faTimesCircle}
                    className="align-middle mr-2 text-danger"
                />
              </a>
            </Col>
          </Row>
      )
    });

    return (
        <Row form>
          <Col sm="2">
            <FormGroup>
              <Label>Category</Label>
              <Select
                  className="react-select-container"
                  classNamePrefix="react-select"
                  options={this.state.categories}
                  onChange={this.handleCategoryChange}
              />
            </FormGroup>
          </Col>
          <Col sm="5">
            <FormGroup>
              <Label>Keyword Search</Label>
              <AsyncCreatable
                  placeholder={"Search-for and select keywords..."}
                  className={"react-select-container"}
                  classNamePrefix={"react-select"}
                  loadOptions={this.keywordAutocomplete}
                  onChange={this.handleKeywordSelect}
                  controlShouldRenderValue={false}
                  isDisabled={!this.state.category}
                  createOptionPosition={"first"}
              />
            </FormGroup>
          </Col>
          <Col sm="5">
            <Row>
              <Col xs={12}>
                <Label>Selected</Label>
              </Col>
            </Row>
            {selectedKeywords}
          </Col>
        </Row>
    );

  }

}