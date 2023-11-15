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

import React from "react";
import StudyListView from "./pages/studyList/StudyListView";
import StudyDetailsView from "./pages/studyDetails/StudyDetailsView";
import StudyFormView from "./pages/studyForm/StudyFormView";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AssayFormView from "./pages/assayForm/AssayFormView";
import AssayDetailsView from "./pages/assayDetails/AssayDetailsView";
import Error, {ErrorBoundary} from "./pages/error/Error";
import ProgramListView from "./pages/programList/ProgramListView";
import ProgramDetailsView from "./pages/programDetails/ProgramDetailsView";
import ProgramFormView from "./pages/programForm/ProgramFormView";
import UserListView from "./pages/userList/UserListView";
import UserDetailsView from "./pages/userDetails/UserDetailsView";
import UserFormView from "./pages/userForm/UserFormView";
import SignInView from "./pages/signIn/SignInView";
import PasswordResetView from "./pages/passwordReset/PasswordResetView";
import AdminDashboardView from "./pages/adminDashboard/AdminDashboardView";
import FrontPageView from "./pages/frontPage/FrontPageView";
import AssayTypeFormView from "./pages/assayTypeForm/AssayTypeFormView";
import AssayListView from "./pages/assayList/AssayListView";
import {TemplateFormView} from "./pages/templateForm/TemplateFormView";
import StudyCollectionFormView from "./pages/studyCollectionForm/StudyCollectionFormView";
import StudyCollectionDetailsView from "./pages/studyCollectionsDetails/StudyCollectionDetailsView";
import StudyCollectionListView from "./pages/studyCollectionList/StudyCollectionListView";
import PasswordResetRequestView from "./pages/passwordResetRequest/PasswordResetRequestView";
import SearchResultsView from "./pages/searchResults/SearchResultsView";
import FileManagerView from "./pages/fileManager/FileManagerView";
import {QueryClient, QueryClientProvider} from "react-query";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
    },
  },
});

const App = props => {

  return (
      <ErrorBoundary>
        <QueryClientProvider client={queryClient}>
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

              {/* File Manager */}
              <Route path={"/file-manager"}
                     element={<FileManagerView />}/>

              {/* Error */}
              <Route path={"/error"}
                     element={<Error />}/>

              {/*404*/}
              <Route element={<Error code={404}/>}/>

            </Routes>
          </BrowserRouter>
        </QueryClientProvider>
      </ErrorBoundary>
  );

}

export default App;
