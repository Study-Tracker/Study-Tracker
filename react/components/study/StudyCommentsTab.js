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
import {Button, Col, Form, Row} from 'react-bootstrap';
import {Comment} from "../comments";
import {MessageCircle} from 'react-feather';
import swal from 'sweetalert';

class StudyCommentsTab extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      comments: this.props.study.comments || [],
      showInput: false,
      newComment: '',
      isError: false
    };
    this.textInput = React.createRef();
    this.toggleInput = this.toggleInput.bind(this);
    this.submitComment = this.submitComment.bind(this);
    this.handleUpdate = this.handleUpdate.bind(this);
    this.updateComment = this.updateComment.bind(this);
    this.deleteComment = this.deleteComment.bind(this);
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

  handleUpdate(text) {
    this.setState({
      newComment: text
    })
  }

  submitComment() {
    const comment = {
      createdBy: this.props.user,
      text: this.state.newComment
    };
    fetch("/api/study/" + this.props.study.code + "/comments", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(comment)
    }).then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error("Failed to create comment");
      }
    }).then(newComment => {
      this.setState({
        comments: [...this.state.comments, newComment],
        newComment: '',
        showInput: false
      });
    })
  }

  updateComment(comment) {
    fetch("/api/study/" + this.props.study.code + "/comments/" + comment.id, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(comment)
    }).then(response => {
      if (response.ok) {
        return response.json();
      }
      swal("Your comment failed to update.",
          "Please try again. If you continue to experience this issues, contact the helpdesk for support.");
      throw new Error("Failed to update comment");
    }).then(json => {
      let comments = this.state.comments;
      for (let i = 0; i < comments.length; i++) {
        if (comments[i].createdAt === json.createdAt
            && comments[i].createdBy.accountName
            === json.createdBy.accountName) {
          comments[i] = json;
        }
      }
      this.setState({
        comments: comments
      });
    })
  }

  deleteComment(comment) {
    fetch("/api/study/" + this.props.study.code + "/comments/" + comment.id, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }).then(response => {
      if (response.ok) {
        const comments = this.state.comments.filter(c => c.id !== comment.id);
        this.setState({
          comments: comments
        });
      } else {
        swal("Your comment failed to delete.",
            "Please try again. If you continue to experience this issues, contact the helpdesk for support.");
        throw new Error("Failed to delete comment");
      }
    }).catch(e => console.error(e));
  }

  render() {

    let comments = this.state.comments.sort((a, b) => {
      if (a.createdAt > b.createdAt) {
        return 1;
      } else if (a.createdAt < b.createdAt) {
        return -1;
      } else {
        return 0;
      }
    }).map((comment, i) => {
      return (
          <React.Fragment key={'hr-' + i}>
            {
              i > 0
                ? (
                    <Col sm={12}>
                      <hr/>
                    </Col>
                ) : ""
            }
            <Col key={'thread-' + comment.createdAt} sm={12}>
              <Comment
                  comment={comment} user={this.props.user}
                  handleUpdate={this.updateComment}
                  handleDelete={this.deleteComment}
              />
            </Col>
          </React.Fragment>
      )
    })

    let content = comments.length > 0 ? comments : (
        <Col sm={12}>
          <div className={"text-center"}>
            <h4>No comments have been added.</h4>
          </div>
        </Col>
    );

    return (
        <div>

          <Row>
            {content}
          </Row>

          <Row>
            <Col sm={12}>
              <div className="d-flex mt-3">
                <div className="flex-grow-1 ms-3">

                  <div className="mb-2 text-center" hidden={this.state.showInput}>
                    <Button variant={'info'} onClick={() => this.toggleInput()}>
                      Add Comment
                      &nbsp;
                      <MessageCircle className="feather align-middle mb-1"/>
                    </Button>
                  </div>

                  <div className="mb-2" hidden={!this.state.showInput}>

                    <Form.Group className="mb-2">
                      <Form.Label>New Comment</Form.Label>
                      <Form.Control
                          ref={this.textInput}
                          as={'textarea'}
                          rows={3}
                          value={this.state.newComment}
                          onChange={(e) => this.handleUpdate(e.target.value)}
                      />
                    </Form.Group>

                    <Button variant={'secondary'}
                            onClick={() => this.toggleInput()}>
                      Cancel
                    </Button>
                    &nbsp;&nbsp;
                    <Button variant={'primary'} onClick={this.submitComment}>
                      Submit
                    </Button>

                  </div>

                </div>
              </div>
            </Col>
          </Row>

        </div>
    );
  }

}

export default StudyCommentsTab;