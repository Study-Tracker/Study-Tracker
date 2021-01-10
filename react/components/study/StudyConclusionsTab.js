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
      conclusions: this.props.study.conclusions
    };
    this.toggleModal = this.toggleModal.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  toggleModal() {
    this.setState({
      modalIsOpen: !this.state.modalIsOpen
    })
  }

  handleSubmit(conclusions) {
    fetch("/api/study/" + this.props.study.code + "/conclusions", {
      method: !!conclusions.id ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(conclusions)
    }).then(response => {
      if (response.ok) {
        this.setState({
          conclusions
        });
        this.toggleModal();
      }
    }).catch(e => {
      console.error(e);
    })
  }

  render() {

    return (
        <div>

          <Conclusions
              conclusions={this.state.conclusions}
              toggleModal={this.toggleModal}
              isSignedIn={!!this.props.user}
          />

          <ConclusionsModal
              isOpen={this.state.modalIsOpen}
              toggleModal={this.toggleModal}
              conclusions={this.state.conclusions}
              handleSubmit={this.handleSubmit}
              user={this.props.user}
          />

        </div>
    );
  }

}

export default StudyConclusionsTab;