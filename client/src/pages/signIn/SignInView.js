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

import React, {useEffect, useState} from "react";
import {useLocation} from "react-router-dom";
import NoNavWrapper from "../../common/structure/NoNavWrapper";
import SignInForm from "./SignInForm";
import axios from "axios";

const qs = require('qs');

const SignInView = props => {

  const location = useLocation();
  const params = qs.parse(location.search,
      {ignoreQueryPrefix: true});
  const isError = params.hasOwnProperty("error");
  const message = params.message || null;
  const [ssoOptions, setSsoOptions] = useState({});

  useEffect(() => {
    axios.get("/auth/options")
    .then(response => setSsoOptions(response.data));
  }, []);

  console.debug("SSO Options: ", ssoOptions);

  return (
      <NoNavWrapper>
        <SignInForm
            isError={isError}
            message={message}
            ssoOptions={ssoOptions}
        />
      </NoNavWrapper>
  );

}

export default SignInView;