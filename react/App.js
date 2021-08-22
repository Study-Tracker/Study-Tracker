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
import StudyListView from "./views/StudyListView";
import {Route, Router} from "react-router";
import StudyDetailsView from "./views/StudyDetailsView";
import {createBrowserHistory} from 'history';
import StudyFormView from "./views/StudyFormView";
import store from "./redux/store";
import {Provider} from "react-redux";
import ReduxToastr from "react-redux-toastr";
import {Switch} from "react-router-dom";
import AssayFormView from './views/AssayFormView'
import AssayDetailsView from "./views/AssayDetailsView";
import Error, {ErrorBoundary} from "./views/Error";
import ScrollToTop from "./structure/ScrollToTop";
import ProgramListView from "./views/ProgramListView";
import ProgramDetailsView from "./views/ProgramDetailsView";
import ProgramFormView from "./views/ProgramFormView";
import UserListView from "./views/UserListView";
import UserDetailsView from "./views/UserDetailsView";
import UserFormView from "./views/UserFormView";
import SignInView from "./views/SignInView";
import PasswordResetView from "./views/PasswordResetView";
import AdminDashboardView from "./views/AdminDashboardView";
import FrontPageView from "./views/FrontPageView";
import AssayTypeFormView from "./views/AssayTypeFormView";
import AssayListView from "./views/AssayListView";
import {TemplateFormView} from './views/TemplateFormView';
import StudyCollectionFormView from "./views/StudyCollectionFormView";
import StudyCollectionDetailsView from "./views/StudyCollectionDetailsView";
import StudyCollectionListView from "./views/StudyCollectionListView";
import PasswordResetRequestView from "./views/PasswordResetRequestView";

export const history = createBrowserHistory();

export default class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
        <ErrorBoundary>
          <Provider store={store}>
            <Router history={history}>
              <ScrollToTop>
                <Switch>

                  {/*Home page */}
                  <Route exact path={["/"]}
                         render={props =>
                             <FrontPageView {...props} />}
                  />

                  {/* Study List */}
                  <Route exact path={["/studies"]}
                         render={props =>
                             <StudyListView {...props} title={"All Studies"}/>}
                  />

                  {/* New study*/}
                  <Route exact path={"/studies/new"}
                         render={props => <StudyFormView {...props} />}/>

                  {/*Study details*/}
                  <Route exact path={"/study/:studyCode"}
                         render={props => <StudyDetailsView {...props} />}/>

                  {/* Edit study*/}
                  <Route exact path={"/study/:studyCode/edit"}
                         render={props => <StudyFormView {...props} />}/>

                  {/* Assay List */}
                  <Route exact path={["/assays"]}
                         render={props => <AssayListView {...props}
                                                         title={"All Assays"}/>}/>

                  {/*New assay*/}
                  <Route exact path={"/study/:studyCode/assays/new"}
                         render={props => <AssayFormView {...props} />}/>

                  {/*Assay details*/}
                  <Route exact path={"/study/:studyCode/assay/:assayCode"}
                         render={props => <AssayDetailsView {...props} />}/>

                  {/*Edit assay*/}
                  <Route exact path={"/study/:studyCode/assay/:assayCode/edit"}
                         render={props => <AssayFormView {...props} />}/>

                  {/*Program list*/}
                  <Route
                      exact
                      path={"/programs"}
                      render={props => <ProgramListView {...props} />}
                  />

                  {/*Program details*/}
                  <Route exact path={"/program/:programId"}
                         render={props => <ProgramDetailsView {...props} />}/>

                  {/*New Program*/}
                  <Route exact path={"/programs/new"}
                         render={props => <ProgramFormView {...props} />}/>

                  {/*Edit Program*/}
                  <Route exact path={"/program/:programId/edit"}
                         render={props => <ProgramFormView {...props} />}/>

                  {/* User list */}
                  <Route
                      exact
                      path={"/users"}
                      render={props => <UserListView {...props} />}
                  />

                  {/*User details*/}
                  <Route exact path={"/user/:userId"}
                         render={props => <UserDetailsView {...props} />}/>

                  {/*New User*/}
                  <Route exact path={"/users/new"}
                         render={props => <UserFormView {...props} />}/>

                  {/*Edit User*/}
                  <Route exact path={"/users/:userId/edit"}
                         render={props => <UserFormView {...props} />}/>

                  {/* Collection list */}
                  <Route
                      exact
                      path={"/collections"}
                      render={props => <StudyCollectionListView {...props} />}
                  />

                  {/*New Collection*/}
                  <Route exact path={"/collections/new"}
                         render={props => <StudyCollectionFormView {...props} />}/>

                  {/*Edit Collection*/}
                  <Route exact path={"/collection/:collectionId/edit"}
                         render={props => <StudyCollectionFormView {...props} />}/>

                  {/*Collection details*/}
                  <Route exact path={"/collection/:collectionId"}
                         render={props => <StudyCollectionDetailsView {...props} />}/>

                  {/* Sign in */}
                  <Route exact path={"/login"}
                         render={props => <SignInView {...props} />}/>

                  <Route exact path={"/auth/passwordresetrequest"}
                         render={props => <PasswordResetRequestView {...props} />}/>

                  <Route exact path={"/auth/passwordreset"}
                         render={props => <PasswordResetView {...props} />}/>

                  {/* Assay Type Form */}
                  <Route exact path={"/assaytypes/new"}
                         render={props => <AssayTypeFormView {...props} />}/>

                  <Route exact path={"/assaytypes/:assayTypeId/edit"}
                         render={props => <AssayTypeFormView {...props} />}/>

                  <Route
                     exact
                     path="/template-types/new"
                     render={ props => <TemplateFormView { ...props } /> }
                  />

                  {/* Admin */}

                  <Route exact path={"/admin"}
                         render={props => <AdminDashboardView {...props} />}/>

                  {/* Error */}
                  <Route exact path={"/error"}
                         render={props => <Error {...props} />}/>

                  {/*404*/}
                  <Route render={props => <Error {...props} code={404}/>}/>

                </Switch>
              </ScrollToTop>
            </Router>
            <ReduxToastr
                timeOut={5000}
                newestOnTop={true}
                position="top-right"
                transitionIn="fadeIn"
                transitionOut="fadeOut"
                progressBar
                closeOnToastrClick
            />
          </Provider>
        </ErrorBoundary>
    );
  }

}
