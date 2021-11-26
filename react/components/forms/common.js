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
import {Form} from 'react-bootstrap';

export const FormGroup = (props) => {
  return (
      <Form.Group className={"mb-3"}>
        {props.children}
      </Form.Group>

  )
}

export class InputWrapper extends React.Component {

  render() {
    return (
        <div className="form-group row justify-content-center">
          <label className="col-sm-2 col-form-label">{this.props.label}</label>
          <div className="col-sm-8">
            {this.props.children}
          </div>
          <div className="col-sm-1">
            <i className="fa fa-2x fa-info-circle text-muted"></i>
          </div>
        </div>
    );
  }
}