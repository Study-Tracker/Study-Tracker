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
import SideBar from "./SideBar";
import NavBar from "./NavBar";
import Footer from "./Footer";

const StandardWrapper = ({children}) => (
    <React.Fragment>
      <div className="wrapper">
        <SideBar/>
        <div className="main">
          <NavBar/>
          <div className="content">
            {children}
          </div>
          <Footer/>
        </div>
      </div>
    </React.Fragment>
);

export default StandardWrapper;