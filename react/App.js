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
import StudyFormView from "./views/StudyForm";
import store from "./redux/store";
import {Provider} from "react-redux";
import ReduxToastr from "react-redux-toastr";
import {Switch} from "react-router-dom";
import AssayFormView from './views/AssayForm'
import AssayDetailsView from "./views/AssayDetailsView";
import Error, {ErrorBoundary} from "./views/Error";
import ScrollToTop from "./structure/ScrollToTop";

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

                  {/*Home page*/}
                  <Route exact path={["/", "/studies"]}
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

                  {/*New assay*/}
                  <Route exact path={"/study/:studyCode/assays/new"}
                         render={props => <AssayFormView {...props} />}/>

                  {/*Assay details*/}
                  <Route exact path={"/study/:studyCode/assay/:assayCode"}
                         render={props => <AssayDetailsView {...props} />}/>

                  {/*Edit assay*/}
                  <Route exact path={"/study/:studyCode/assay/:assayCode/edit"}
                         render={props => <AssayFormView {...props} />}/>

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
