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
import StudyListView from "./views/StudyListView";
import StudyDetailsView from "./views/StudyDetailsView";
import {createBrowserHistory} from "history";
import StudyFormView from "./views/StudyFormView";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AssayFormView from "./views/AssayFormView";
import AssayDetailsView from "./views/AssayDetailsView";
import Error, {ErrorBoundary} from "./views/Error";
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
import {TemplateFormView} from "./views/TemplateFormView";
import StudyCollectionFormView from "./views/StudyCollectionFormView";
import StudyCollectionDetailsView from "./views/StudyCollectionDetailsView";
import StudyCollectionListView from "./views/StudyCollectionListView";
import PasswordResetRequestView from "./views/PasswordResetRequestView";
import SearchResultsView from "./views/SearchResultsView";

export const history = createBrowserHistory();

export default class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
        <ErrorBoundary>
          <BrowserRouter>
            <Routes>

              {/*Home page */}
              <Route path={"/"}
                     element={<FrontPageView />}
              />

              {/* Study List */}
              <Route path={"/studies"}
                     element={<StudyListView title={"All Studies"}/>}
              />

              {/* New study*/}
              <Route path={"/studies/new"}
                     element={<StudyFormView />}/>

              {/*Study details*/}
              <Route path={"/study/:studyCode"}
                     element={<StudyDetailsView />}/>

              {/* Edit study*/}
              <Route path={"/study/:studyCode/edit"}
                     element={<StudyFormView />}/>

              {/* Assay List */}
              <Route path={"/assays"}
                     element={<AssayListView title={"All Assays"}/>}/>

              {/*New assay*/}
              <Route path={"/study/:studyCode/assays/new"}
                     element={<AssayFormView />}/>

              {/*Assay details*/}
              <Route path={"/study/:studyCode/assay/:assayCode"}
                     element={<AssayDetailsView />}/>

              {/*Edit assay*/}
              <Route path={"/study/:studyCode/assay/:assayCode/edit"}
                     element={<AssayFormView />}/>

              {/*Program list*/}
              <Route
                  exact
                  path={"/programs"}
                  element={<ProgramListView />}
              />

              {/*Program details*/}
              <Route path={"/program/:programId"}
                     element={<ProgramDetailsView />}/>

              {/*New Program*/}
              <Route path={"/programs/new"}
                     element={<ProgramFormView />}/>

              {/*Edit Program*/}
              <Route path={"/program/:programId/edit"}
                     element={<ProgramFormView />}/>

              {/* User list */}
              <Route
                  exact
                  path={"/users"}
                  element={<UserListView />}
              />

              {/*User details*/}
              <Route path={"/user/:userId"}
                     element={<UserDetailsView />}/>

              {/*New User*/}
              <Route path={"/users/new"}
                     element={<UserFormView />}/>

              {/*Edit User*/}
              <Route path={"/users/:userId/edit"}
                     element={<UserFormView />}/>

              {/* Collection list */}
              <Route
                  exact
                  path={"/collections"}
                  element={<StudyCollectionListView />}
              />

              {/*New Collection*/}
              <Route path={"/collections/new"}
                     element={<StudyCollectionFormView />}/>

              {/*Edit Collection*/}
              <Route path={"/collection/:collectionId/edit"}
                     element={<StudyCollectionFormView />}/>

              {/*Collection details*/}
              <Route path={"/collection/:collectionId"}
                     element={<StudyCollectionDetailsView />}/>

              {/* Sign in */}
              <Route path={"/login"}
                     element={<SignInView />}/>

              <Route path={"/auth/passwordresetrequest"}
                     element={<PasswordResetRequestView />}/>

              <Route path={"/auth/passwordreset"}
                     element={<PasswordResetView />}/>

              {/* Assay Type Form */}
              <Route path={"/assaytypes/new"}
                     element={<AssayTypeFormView />}/>

              <Route path={"/assaytypes/:assayTypeId/edit"}
                     element={<AssayTypeFormView />}/>

              <Route
                  path="/template-types/new"
                  element={<TemplateFormView />}
              />

              {/* Admin */}

              <Route path={"/admin"}
                     element={<AdminDashboardView />}/>

              {/* Search */}

              <Route path={"/search"}
                     element={<SearchResultsView />}/>

              {/* Error */}
              <Route path={"/error"}
                     element={<Error />}/>

              {/*404*/}
              <Route element={<Error code={404}/>}/>

            </Routes>  
          </BrowserRouter>
        </ErrorBoundary>
    );
  }

}
