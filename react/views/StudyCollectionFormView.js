import React from "react";
import {connect} from "react-redux";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import StudyCollectionForm from "../components/forms/StudyCollectionForm";

class StudyCollectionFormView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      collectionId: props.match.params.collectionId || null,
      isLoaded: false,
      isError: false
    }
  }

  componentDidMount() {
    fetch("/api/studycollection")
    .then(response => response.json())
    .then(collections => {
      if (!!this.state.collectionId) {
        const collection = collections.find(p => String(p.id) === this.state.collectionId);
        this.setState({
          collection,
          collections,
          isLoaded: true
        });
      } else {
        this.setState({
          collections,
          isLoaded: true
        });
      }
    }).catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    });
  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (!!this.props.user && this.state.isLoaded) {
      content = <StudyCollectionForm
          collection={this.state.collection}
          collections={this.state.collections}
          user={this.props.user}
      />;
    }
    return (
        <NoSidebarPageWrapper>
          {content}
        </NoSidebarPageWrapper>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(StudyCollectionFormView);