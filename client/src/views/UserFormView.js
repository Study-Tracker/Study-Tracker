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
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import UserForm from "../components/forms/UserForm";
import {useSelector} from "react-redux";
import axios from "axios";

const UserFormView = props => {
  
  const [state, setState] = useState({
    userId: props.match.params.userId || null,
    isLoaded: false,
    isError: false,
  });
  const user = useSelector(s => s.user.value);

  useEffect(() => {

    // Programs
    axios.get("/api/user")
    .then(response => {
      const users = response.data;
      if (!!state.userId) {
        const user = users.find(p => p.id === parseInt(state.userId));
        setState({ 
          ...state,
          selectedUser: user,
          users: users,
          isLoaded: true
        });
      } else {
        setState({ 
          ...state,
          users: users,
          isLoaded: true
        });
      }
    }).catch(error => {
      setState({ 
        ...state,
        isError: true,
        error: error
      });
    });

  }, []);

  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (!!user && state.isLoaded) {
    content = <UserForm
        user={state.selectedUser}
        users={state.users}
    />;
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

export default UserFormView;
