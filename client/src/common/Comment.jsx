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

import React, {createRef, useState} from "react";
import {Button, Form} from "react-bootstrap";
import {User} from 'react-feather';
import {Form as FormikForm, Formik} from 'formik';
import dateFormat from "dateformat";
import PropTypes from "prop-types";
import * as yup from "yup";
import axios from "axios";
import swal from "sweetalert2";

const Comment = props => {

  const {comment, user, handleUpdate, handleDelete, study} = props;
  const textInput = createRef();
  const [showInput, setShowInput] = useState(false);
  const commentDefault = {...comment};

  const commentSchema = yup.object().shape({
    text: yup.string().required("Comment is required"),
    createdBy: yup.object()
  });

  const toggleInput = () => {
    const show = !showInput;
    setShowInput(show);
    if (show) {
      textInput.current.focus();
    }
  }

  const handleFormSubmit = (values, {setSubmitting}) => {
    axios.put("/api/internal/study/" + study.code + "/comments/" + comment.id, values)
    .then(response => {
      setSubmitting(false);
      toggleInput();
      handleUpdate(response.data);
    })
    .catch(error => {
      setSubmitting(false);
      console.error(error);
      swal.fire({
          title: "Your comment failed to update.",
          text: "Please try again. If you continue to experience this issues, contact the helpdesk for support.",
          icon: "error",
      });
    });
  };

  return (
      <div className="d-flex">

        <div className="stat">
          <User
              size={36}
              className="align-middle text-info me-4"
          />
        </div>

        <div className="flex-grow-1 ms-3">

          <small className="float-end text-navy">
            posted {dateFormat(new Date(comment.createdAt),
              'mm/dd/yy @ h:MM TT')}
            {
              comment.updatedAt && (
                  <span>
                    , edited {dateFormat(new Date(comment.updatedAt), 'mm/dd/yy @ h:MM TT')}
                  </span>
              )
            }
          </small>

          <p className="mb-2">
            <strong>{comment.createdBy.displayName}</strong>
          </p>

          <div className="mb-3">

            <div hidden={showInput}>
              {comment.text}
            </div>

            <div hidden={!showInput}>

              <Formik
                  initialValues={commentDefault}
                  onSubmit={handleFormSubmit}
                  validationSchema={commentSchema}
              >
                {({
                  values,
                  errors,
                  touched,
                  handleChange,
                  isSubmitting
                }) => (
                    <FormikForm>

                      <div className="mb-2">
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
                      </div>

                      <div>
                        <Button
                            variant={'secondary'}
                            onClick={() => toggleInput()}
                        >
                          Cancel
                        </Button>
                        &nbsp;
                        <Button variant={'primary'} type={"submit"} disabled={isSubmitting}>
                          {isSubmitting ? 'Saving...' : 'Save'}
                        </Button>
                      </div>

                    </FormikForm>

                )}
              </Formik>

            </div>
          </div>

          {
            !!user && comment.createdBy.accountName === user.accountName
                ? (
                    <div hidden={showInput}>
                      <Button size='sm' variant='warning'
                              onClick={toggleInput}>Edit</Button>
                      &nbsp;&nbsp;
                      <Button size='sm' variant='danger'
                              onClick={() => handleDelete(comment)}>Delete</Button>
                    </div>
                ) : ''
          }

        </div>

      </div>
  );

}

Comment.propTypes = {
  study: PropTypes.object.isRequired,
  comment: PropTypes.object.isRequired,
  user: PropTypes.object,
  handleUpdate: PropTypes.func.isRequired,
  handleDelete: PropTypes.func.isRequired
}

export default Comment;