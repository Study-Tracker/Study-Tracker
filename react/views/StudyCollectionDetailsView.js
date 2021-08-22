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
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import StandardWrapper from "../structure/StandardWrapper";
import {connect} from 'react-redux';
import StudyCollectionDetails
  from "../components/collections/StudyCollectionDetails";
import swal from "sweetalert";

class StudyCollectionDetailsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      collectionId: props.match.params.collectionId,
      isLoaded: false,
      isError: false
    };
    this.handleRemoveStudy = this.handleRemoveStudy.bind(this);
  }

  handleRemoveStudy(id) {
    swal({
      title: "Are you sure you want to remove this study from the collection?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        fetch("/api/studycollection/" + this.state.collection.id + "/" + id, {
          method: 'DELETE',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(response => {
          let collection = this.state.collection;
          collection.studies = collection.studies.filter(s => s.id === id);
          this.setState({collection});
        })
        .catch(error => {
          console.error(error);
        })
      }
    });

  }

  componentDidMount() {
    fetch("/api/studycollection/" + this.state.collectionId)
    .then(response => response.json())
    .then(collection => {
      this.setState({
        collection: collection,
        isLoaded: true
      });
      console.log(collection);
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    })
  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (this.state.isLoaded) {
      content = <StudyCollectionDetails
          collection={this.state.collection}
          user={this.props.user}
          handleRemoveStudy={this.handleRemoveStudy}
      />;
    }
    return (
        <StandardWrapper {...this.props}>
          {content}
        </StandardWrapper>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(StudyCollectionDetailsView);