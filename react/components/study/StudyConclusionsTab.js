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
import {Conclusions, ConclusionsModal} from "../conclusions";

class StudyConclusionsTab extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      conclusions: props.study.conclusions,
      updatedConclusions: !!props.study.conclusions ? {
        ...props.study.conclusions,
        lastModifiedBy: props.user
      } : {
        content: '',
        createdBy: props.user
      }
    };
    this.showModal = this.showModal.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleUpdate = this.handleUpdate.bind(this);
  }

  showModal(bool) {
    this.setState({
      modalIsOpen: bool
    })
  }

  handleUpdate(content) {
    this.setState({
      updatedConclusions: {
        ...this.state.updatedConclusions,
        content: content
      }
    })
  }

  handleSubmit() {
    fetch("/api/study/" + this.props.study.code + "/conclusions", {
      method: !!this.state.conclusions ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(this.state.updatedConclusions)
    })
    .then(response => response.json())
    .then(json => {
      this.setState({
        conclusions: json,
        updatedConclusions: json
      });
      this.showModal(false);
    }).catch(e => {
      console.error(e);
    })
  }

  render() {

    return (
        <div>

          <Conclusions
              conclusions={this.state.conclusions}
              showModal={this.showModal}
              isSignedIn={!!this.props.user}
          />

          <ConclusionsModal
              isOpen={this.state.modalIsOpen}
              showModal={this.showModal}
              conclusions={this.state.updatedConclusions}
              handleSubmit={this.handleSubmit}
              handleUpdate={this.handleUpdate}
              user={this.props.user}
          />

        </div>
    );
  }

}

export default StudyConclusionsTab;