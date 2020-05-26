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

import {
  Button,
  Col,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row
} from "reactstrap";
import React, {useMemo} from "react";
import {useDropzone} from 'react-dropzone';
import {
  faCaretDown,
  faCaretRight,
  faFile,
  faFolder
} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {CardLoadingMessage} from "./loading";
import {DismissableAlert} from "./errors";

const baseStyle = {
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '20px',
  borderWidth: 2,
  borderRadius: 2,
  borderColor: '#eeeeee',
  borderStyle: 'dashed',
  backgroundColor: '#fafafa',
  color: '#bdbdbd',
  outline: 'none',
  transition: 'border .24s ease-in-out'
};

const activeStyle = {
  borderColor: '#2196f3'
};

const acceptStyle = {
  borderColor: '#00e676'
};

const rejectStyle = {
  borderColor: '#ff1744'
};

/**
 * Converts a byte size integer into a file size display string
 *
 * @param size
 * @returns {string}
 */
const formatFileSize = (size) => {
  if (size >= 1000000000) {
    return (size / 1000000000).toFixed(2) + " gb";
  } else if (size >= 1000000) {
    return (size / 1000000).toFixed(2) + " mb";
  } else if (size >= 1000) {
    return (size / 1000).toFixed(2) + " kb";
  } else {
    return size + " bytes";
  }
};

class Folder extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      folder: props.folder,
      isExpanded: false,
      depth: props.depth || 0
    }
    this.toggleExpanded = this.toggleExpanded.bind(this);
  }

  toggleExpanded() {
    this.setState({
      isExpanded: !this.state.isExpanded
    });
  }

  render() {
    return (
        <li>
          <a onClick={this.toggleExpanded}>
            <FontAwesomeIcon
                icon={this.state.isExpanded ? faCaretDown : faCaretRight}
                style={{width: "10px"}}
            />
            &nbsp;&nbsp;
            <FontAwesomeIcon icon={faFolder}/> {this.state.folder.name}
          </a>
          <div hidden={!this.state.isExpanded}>
            <FolderContents folder={this.state.folder}
                            depth={this.state.depth + 1}/>
          </div>
        </li>
    )
  }
}

const File = ({file}) => {
  return (
      <li>
        <div className="ml-3">
          <a href={file.url} target="_blank">
            <FontAwesomeIcon icon={faFile}/>
            &nbsp;
            {file.name}
          </a>
          &nbsp;-&nbsp;
          {formatFileSize(file.size)}
        </div>
      </li>
  )
}

const FolderContents = ({folder, depth}) => {
  const subFolders = folder.subFolders
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(f => {
    return <Folder key={"folder-" + f.name} folder={f} depth={depth}/>
  });

  const files = folder.files
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(f => {
    return <File key={"file-" + f.name} file={f}/>;
  });

  const items = [...subFolders, ...files];

  return items.length
      ? (
          <ul className="list-unstyled"
              style={{marginLeft: (depth > 0 ? 1 : 0) + "em"}}>
            {items}
          </ul>
      ) : '';

}

/**
 * Returns a ul list of all files and subfolders within the supplied folder.
 *
 * @param folder
 * @param isError
 * @param isLoaded
 * @returns {*}
 * @constructor
 */
export const StorageFolderFileList = ({folder, isLoaded, isError}) => {

  if (isError) {
    return <DismissableAlert color={'warning'}
                             message={'Failed to load study folder.'}/>
  } else if (isLoaded) {
    if (folder.subFolders.length === 0 && folder.files.length === 0) {
      return (
          <div className={"text-center"}>
            <h4>The study folder is empty.</h4>
          </div>
      );
    } else {
      return <FolderContents folder={folder} depth={1}/>
    }
  } else {
    return <CardLoadingMessage/>;
  }

};

/**
 * Modal for accepting file uploads.
 *
 * @param isOpen
 * @param toggleModal
 * @param handleSubmit
 * @returns {*}
 * @constructor
 */
export const UploadFilesModal = ({isOpen, toggleModal, handleSubmit}) => {

  const {
    acceptedFiles,
    getRootProps,
    getInputProps,
    isDragAccept,
    isDragActive,
    isDragReject
  } = useDropzone();

  const files = acceptedFiles.map(file => (
      <li key={file.path}>
        <FontAwesomeIcon icon={faFile}/> {file.path}
      </li>
  ));

  const style = useMemo(() => ({
    ...baseStyle,
    ...(isDragActive ? activeStyle : {}),
    ...(isDragAccept ? acceptStyle : {}),
    ...(isDragReject ? rejectStyle : {})
  }), [
    isDragActive, isDragReject
  ]);

  return (
      <Modal
          isOpen={isOpen}
          toggle={() => toggleModal()}
          size={"md"}
      >

        <ModalHeader toggle={() => toggleModal()}>
          Upload Files
        </ModalHeader>

        <ModalBody className="m-3">

          <Row form>

            <Col sm={12}>
              <p>
                Uploaded files will be stored with your other study documents
                and
                will be accessible directly in the file system or through the
                Files
                tab.
              </p>
            </Col>

            <Col sm={12}>
              <div {...getRootProps({style})}>
                <input {...getInputProps()} />
                <p>
                  Drag-and-drop files to upload them, or click here to select
                  the files you would like to upload individually.
                </p>
              </div>
            </Col>

            <Col sm={12}></Col>

            <Col sm={12}>
              <h4>To be uploaded:</h4>
              <ul>
                {files}
              </ul>
            </Col>

          </Row>

        </ModalBody>

        <ModalFooter>
          <Button color={"secondary"} onClick={() => toggleModal()}>
            Cancel
          </Button>
          <Button color={"primary"} onClick={() => handleSubmit(acceptedFiles)}>
            Upload
          </Button>
        </ModalFooter>

      </Modal>
  )
};