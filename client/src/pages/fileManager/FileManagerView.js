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

import React from 'react';
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import FileManager from "../../common/fileManager/FileManager";
import {useSearchParams} from "react-router-dom";

const FileManagerView = () => {

  const [searchParams, setSearchParams] = useSearchParams();
  const path = searchParams.has("path") ? searchParams.get('path') : null;
  const locationId = searchParams.has("locationId") ? searchParams.get('locationId') : null;

  return (
      <>
        <div className="wrapper">
          <SideBar/>
          <div className="main">
            <NavBar />
            <div className="content">
              <FileManager path={path} locationId={locationId} />
            </div>
            <Footer/>
          </div>
        </div>
      </>
  )

}

export default FileManagerView;