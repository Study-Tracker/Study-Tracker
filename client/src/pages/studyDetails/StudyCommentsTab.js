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

import React, {createRef, useState} from "react";
import {Button, Col, Form, Row} from 'react-bootstrap';
import Comment from "../../common/Comment";
import {MessageCircle} from 'react-feather';
import swal from 'sweetalert';
import {Form as FormikForm, Formik} from 'formik';
import axios from "axios";
import * as yup from 'yup';
import PropTypes from "prop-types";

const StudyCommentsTab = props => {

  const {user, study} = props;
  const [showInput, setShowInput] = useState(false);
  const [comments, setComments] = useState(study.comments || []);
  const commentDefault = {
    text: ""
  };
  const textInput = createRef();

  const commentSchema = yup.object().shape({
    text: yup.string().required("Comment is required")
  });

  const handleFormSubmit = (values, {resetForm, setSubmitting}) => {
    console.debug("commentFormValues", values);
    setSubmitting(true);
    axios.post("/api/internal/study/" + study.code + "/comments", values)
    .then(response => {
      const newComment = response.data;
      setComments([...comments, newComment]);
      setShowInput(false);
      resetForm();
      setSubmitting(false);
    })
    .catch(error => {
      console.error(error);
      setSubmitting(false);
      swal("Your comment failed to create.",
          "Please try again. If you continue to experience this issues, contact the helpdesk for support.");
    })
  };

  const toggleInput = () => {
    const show = !showInput;
    setShowInput(show);
    if (show) {
      textInput.current.focus();
    }
  }

  const handleCommentUpdate = (comment) => {
    axios.put("/api/internal/study/" + study.code + "/comments/" + comment.id, comment)
    .then(response => {
      let updated = [...comments];
      for (let i = 0; i < updated.length; i++) {
        if (updated[i].createdAt === response.data.createdAt
            && updated[i].createdBy.accountName === response.data.createdBy.accountName) {
          updated[i] = response.data;
        }
      }
      setComments(updated);
    })
    .catch(error => {
      console.error(error);
      swal("Your comment failed to update.",
          "Please try again. If you continue to experience this issues, contact the helpdesk for support.");
    });
  }

  const handleCommentDelete = (comment) => {
    swal({
      title: "Are you sure you want to delete this comment?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/study/" + study.code + "/comments/" + comment.id)
        .then(response => {
          const updated = comments.filter(c => c.id !== comment.id);
          setComments(updated);
        })
        .catch(e => {
          console.error(e)
          swal("Your comment failed to delete.",
              "Please try again. If you continue to experience this issues, contact the helpdesk for support.");
        });
      }
    });
  }

  let commentCards = comments.sort((a, b) => {
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
                study={study}
                comment={comment}
                user={user}
                handleUpdate={handleCommentUpdate}
                handleDelete={handleCommentDelete}
            />
          </Col>
        </React.Fragment>
    )
  })

  let content = commentCards.length > 0
      ? commentCards
      : (
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

                <div className="mb-2 text-center" hidden={showInput}>
                  <Button variant={'info'} onClick={() => toggleInput()}>
                    Add Comment
                    &nbsp;
                    <MessageCircle className="feather align-middle mb-1"/>
                  </Button>
                </div>

                <div className="mb-2" hidden={!showInput}>

                  <Formik
                      initialValues={commentDefault}
                      onSubmit={handleFormSubmit}
                      validationSchema={commentSchema}
                  >
                    {({
                      values,
                      errors,
                      touched,
                      handleChange
                    }) => (
                        <FormikForm>

                          <Form.Group className="mb-2">
                            <Form.Label>New Comment</Form.Label>
                            <Form.Control
                                ref={textInput}
                                as={'textarea'}
                                name={"text"}
                                className={(!!errors.text && touched.text) ? 'is-invalid' : ''}
                                rows={3}
                                value={values.text}
                                onChange={handleChange}
                            />
                            <Form.Control.Feedback type="invalid">
                              {errors.text}
                            </Form.Control.Feedback>
                          </Form.Group>

                          <Button
                              variant={'secondary'}
                              onClick={() => toggleInput()}
                          >
                            Cancel
                          </Button>
                          &nbsp;&nbsp;
                          <Button
                              variant={'primary'}
                              type="submit"
                          >
                            Submit
                          </Button>

                        </FormikForm>

                    )}

                  </Formik>

                </div>

              </div>
            </div>
          </Col>
        </Row>

      </div>
  );

}

StudyCommentsTab.propTypes = {
  study: PropTypes.object.isRequired,
  user: PropTypes.object.isRequired,
}

export default StudyCommentsTab;