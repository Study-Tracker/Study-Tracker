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
import {ProgramDropdown} from "./programs";

export default class StudyFormWizard extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      study: props.study || {}
    }
  }

  componentDidMount() {
    $('#start-date-picker').datepicker({
      todayBtn: "linked",
      keyboardNavigation: false,
      forceParse: false,
      calendarWeeks: true,
      autoclose: true
    });

    $("#study-form").steps({
      bodyTag: "fieldset",
      onStepChanging: function (event, currentIndex, newIndex) {
        if (currentIndex > newIndex) {
          return true; // Always allow going backward even if the current step contains invalid fields!
        }

        // Forbid suppressing "Warning" step if the user is to young
        if (newIndex === 3 && Number($("#age").val()) < 18) {
          return false;
        }

        let form = $(this);

        // Clean up if user went backward before
        if (currentIndex < newIndex) {
          // To remove error styles
          $(".body:eq(" + newIndex + ") label.error", form).remove();
          $(".body:eq(" + newIndex + ") .error", form).removeClass("error");
        }

        // Disable validation on fields that are disabled or hidden.
        form.validate().settings.ignore = ":disabled,:hidden";

        // Start validation; Prevent going forward if false
        return form.valid();
      },
      onStepChanged: function (event, currentIndex, priorIndex) {
        // Suppress (skip) "Warning" step if the user is old enough.
        if (currentIndex === 2 && Number($("#age").val()) >= 18) {
          $(this).steps("next");
        }

        // Suppress (skip) "Warning" step if the user is old enough and wants to the previous step.
        if (currentIndex === 2 && priorIndex === 3) {
          $(this).steps("previous");
        }
      },
      onFinishing: function (event, currentIndex) {
        var form = $(this);

        // Disable validation on fields that are disabled.
        // At this point it's recommended to do an overall check (mean ignoring only disabled fields)
        form.validate().settings.ignore = ":disabled";

        // Start validation; Prevent form submission if false
        return form.valid();
      },
      onFinished: function (event, currentIndex) {
        var form = $(this);

        // Submit form input
        form.submit();
      }
    }).validate({
      errorPlacement: function (error, element) {
        element.before(error);
      },
      rules: {
        confirm: {
          equalTo: "#password"
        }
      }
    });
  }

  handleFormUpdate(data) {
    const study = {
      ...this.state.study,
      ...data
    };
    console.log(study);
    this.setState({
      study: study
    })
  }

  render() {

    return (
        <div>

          <div className="row justify-content-end align-items-center">
            <div className="col">
              <h1>{!!this.state.study.id ? "Edit Study" : "New Study"}</h1>
            </div>
          </div>

          <div className="row">
            <div className="col-12">
              <div className="ibox">
                <div className="ibox-content">
                  <form id="study-form" className="wizard-big">

                    <h1>Overview</h1>

                    <fieldset id="overview-fieldset">
                      <h2>Study Overview</h2>
                      <div className="row">
                        <div className="col-lg-8">

                          <div className="form-group">
                            <label>Name *</label>
                            <input
                                type="text"
                                className="form-control required"
                                defaultValue={this.state.study.name || ''}
                                onChange={(e) => this.handleFormUpdate(
                                    {"name": e.target.value})}
                            />
                          </div>

                          <div className="form-group">
                            <label>Program *</label>
                            <ProgramDropdown
                                programs={this.props.programs}
                                selectedProgram={!!this.state.study.program
                                    ? this.state.study.program.id : -1}
                                handleChange={(e) => {
                                  const program = this.props.programs.filter(
                                      p => {
                                        return p.id === parseInt(
                                            e.target.value);
                                      })[0];
                                  this.handleFormUpdate({"program": program});
                                }}
                            />
                          </div>

                          <div className="form-group">
                            <label>Description *</label>
                            <textarea
                                rows="3"
                                className="form-control required"
                                defaultValue={this.state.study.description
                                || ''}
                                onChange={(e) => this.handleFormUpdate(
                                    {"description": e.target.value})}
                            />
                          </div>

                          <div className="form-group">
                            <label>Status *</label>
                            <select
                                className="form-control"
                                defaultValue={this.state.study.status
                                || 'In Planning'}
                                onChange={(e) => this.handleFormUpdate(
                                    {"status": e.target.value})}
                            >
                              <option>In Planning</option>
                              <option>Active</option>
                              <option>Complete</option>
                              <option>On Hold</option>
                              <option>Deprioritized</option>
                            </select>
                          </div>

                          <div className="form-group">
                            <label>Start Date *</label>
                            <div id="start-date-picker"
                                 className="input-group date">
                              <span className="input-group-addon">
                                <i className="fa fa-calendar"></i>
                              </span>
                              <input
                                  type="text"
                                  className="form-control"
                                  onChange={(e) => this.handleFormUpdate(
                                      {"startDate": $(e.target).getDate()})}
                              />
                            </div>
                          </div>

                        </div>
                        <div className="col-lg-4">
                          <div className="text-justify">
                            <p>
                              Please provide a unique name for your study, a
                              brief description, and select the program that it
                              will be associated with. The study name and
                              program cannot be changed after the study has been
                              created.
                            </p>
                          </div>
                          <div className="text-center">
                            <div style={{marginTop: "20px"}}>
                              <i className="fa fa-clipboard"
                                 style={{
                                   fontSize: "180px",
                                   color: "#e5e5e5"
                                 }}></i>
                            </div>
                          </div>
                        </div>
                      </div>
                    </fieldset>

                    <h1>Team</h1>

                    <fieldset>
                      <h2>Study Team</h2>

                      <div className="row">
                        <div className="col-lg-8">
                          <div className="form-group">
                            <label>Users</label>
                            <input type="text"/>
                          </div>
                        </div>
                      </div>

                      <div className="col-lg-4">
                        <div className="text-justify">
                          <p>
                            Select all of the members of the study team. One
                            member must be selected to be the study owner. The
                            study owner will be the primary contact person for
                            this study.
                          </p>
                        </div>
                        <div className="text-center">
                          <div style={{marginTop: "20px"}}>
                            <i className="fa fa-users"
                               style={{
                                 fontSize: "180px",
                                 color: "#e5e5e5"
                               }}></i>
                          </div>
                        </div>
                      </div>

                    </fieldset>

                  </form>
                </div>
              </div>
            </div>
          </div>

        </div>
    );
  }

}