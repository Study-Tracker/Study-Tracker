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
import React, {useState} from 'react';
import {Table} from 'react-bootstrap';
import {Download, File, Folder, Image, Link} from "react-feather";

const FileManagerTable = ({folder, handlePathChange}) => {
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectedItemIndex, setSelectedItemIndex] = useState(null);

  const handleItemClick = (item, index) => {
    setSelectedItem(item);
    setSelectedItemIndex(index);
  };

  const getIcon = (file) => {
    if (file.type === "folder") {
      return <Folder />;
    } else if (file.type === "image") {
      return <Image />;
    } else {
      return <File />;
    }
  }

  const getFileSize = (file) => {
    if (file.type === "folder") {
      return "";
    } else {
      return file.size + " bytes";
    }
  }

  const renderTableRows = () => {
    const folderRows = folder.subFolders
    .sort((a, b) => a.name.localeCompare(b.name))
    .map((f, index) => {
      return (
          <tr
              key={index}
              onClick={() => handleItemClick(f, index)}
              className={selectedItemIndex === index ? 'selected' : ''}
          >
            <td><Folder /> {f.name}</td>
            <td>{f.totalSize || '-'}</td>
            <td>{f.lastModified}</td>
            <td className="table-action">
              <Download className="align-middle me-1" size={18} />
              <Link className="align-middle" size={18} />
            </td>
          </tr>
      );
    });
    const fileRows = folder.files
    .sort((a, b) => a.name.localeCompare(b.name))
    .map((f, index) => {
      index += folderRows.length;
      return (
        <tr
          key={index}
          onClick={() => handleItemClick(f, index)}
          className={selectedItemIndex === index ? 'selected' : ''}
        >
          <td><File /> {f.name}</td>
          <td>{f.size}</td>
          <td>{new Date(f.lastModified).toLocaleDateString()}</td>
          <td className="table-action">
            <Download className="align-middle me-1" size={18} />
            <Link className="align-middle" size={18} />
          </td>
        </tr>
      );
    });
    return [...folderRows, ...fileRows];
  };

  return (
    <Table hover>
      <thead>
        <tr>
          <th style={{width: "40%"}}>Name</th>
          <th style={{width: "20%"}}>Size</th>
          <th>Last Modified Date</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>{renderTableRows()}</tbody>
    </Table>
  );
}

export default FileManagerTable;
