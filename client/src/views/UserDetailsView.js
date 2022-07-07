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

import React, {useEffect, useState} from "react";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import StandardWrapper from "../structure/StandardWrapper";
import UserDetails from "../components/user/UserDetails";
import axios from "axios";
import {useParams} from "react-router-dom";

const UserDetailsView = props => {

  const params = useParams();
  const [state, setState] = useState({
    userId: params.userId,
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    axios.get("/api/user/" + state.userId)
    .then(async response => {
      const user = response.data;
      axios.get("/api/study?user=" + user.id)
      .then(r2 => {
        setState({ 
          ...state,
          user: user,
          studies: r2.data,
          isLoaded: true
        });
        console.debug(user);
      })
    })
    .catch(error => {
      console.error(error);
      setState({ 
        ...state,
        isError: true,
        error: error
      });
    })
  }, []);

  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (state.isLoaded) {
    content = <UserDetails targetUser={state.user}
                           studies={state.studies}
                           user={props.user}/>;
  }
  return (
      <StandardWrapper {...props}>
        {content}
      </StandardWrapper>
  );


}

export default UserDetailsView;