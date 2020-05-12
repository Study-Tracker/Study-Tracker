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
import {Button, Input, Media} from "reactstrap";
import {User} from 'react-feather';
import swal from "sweetalert";

const dateFormat = require('dateformat');

export class Comment extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      text: props.comment.text,
      showInput: false,
      updatedComment: props.comment
    };
    this.textInput = React.createRef();
    this.handleUpdate = this.handleUpdate.bind(this);
    this.toggleInput = this.toggleInput.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
  }

  toggleInput() {
    const show = !this.state.showInput;
    this.setState({
      showInput: show
    });
    if (show) {
      this.textInput.current.focus();
    }
  }

  handleUpdate(value) {
    const c = {
      ...this.state.updatedComment,
      text: value
    };
    this.setState({
      updatedComment: c
    });
  }

  handleDelete() {
    swal({
      title: "Are you sure you want to delete this comment?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        this.props.handleDelete(this.props.comment);
      }
    });
  }

  render() {
    return (
        <Media>

          <User
              size={36}
              className="align-middle text-info mr-4"
          />

          <Media body>

            <small className="float-right text-navy">
              posted {dateFormat(new Date(this.props.comment.createdAt),
                'mm/dd/yy @ h:MM TT')}
              {
                !!this.props.comment.updatedAt
                    ? (
                        <span>, edited {dateFormat(
                            new Date(this.props.comment.updatedAt),
                            'mm/dd/yy @ h:MM TT')}</span>
                    ) : ''
              }
            </small>

            <p className="mb-2">
              <strong>{this.props.comment.createdBy.displayName}</strong>
            </p>

            <div className="mb-3">

              <div hidden={this.state.showInput}>
                {this.props.comment.text}
              </div>

              <div hidden={!this.state.showInput}>

                <div className="mb-2">
                  <Input
                      ref={this.textInput}
                      type={'textarea'}
                      defaultValue={this.props.comment.text}
                      onChange={(e) => this.handleUpdate(e.target.value)}
                  />
                </div>

                <div>
                  <Button color={'secondary'}
                          onClick={() => this.toggleInput()}>
                    Cancel
                  </Button>
                  &nbsp;
                  <Button color={'primary'} onClick={() => {
                    this.toggleInput();
                    this.props.handleUpdate(this.state.updatedComment)
                  }}>
                    Submit
                  </Button>
                </div>

              </div>
            </div>

            {
              !!this.props.user && this.props.comment.createdBy.accountName
              === this.props.user.accountName
                  ? (
                      <div hidden={this.state.showInput}>
                        <Button size='sm' color='warning'
                                onClick={() => this.toggleInput()}>Edit</Button>
                        &nbsp;&nbsp;
                        <Button size='sm' color='danger'
                                onClick={this.handleDelete}>Delete</Button>
                      </div>
                  ) : ''
            }

          </Media>

        </Media>
    );
  }

}
