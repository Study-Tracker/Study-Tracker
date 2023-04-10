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

import React, {useContext, useEffect, useState} from "react";
import PropTypes from "prop-types";
import IllustrationWidget from "./IllustrationWidget";
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import LoadingMessageWidget from "./LoadingMessageWidget";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFileLines, faRefresh} from "@fortawesome/free-solid-svg-icons";
import NotyfContext from "../../context/NotyfContext";
import IconWidget from "./IconWidget";

const PrimaryStorageFolderWidget = ({record}) => {

  const [folder, setFolder] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isRepaird, setIsRepaird] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const notyf = useContext(NotyfContext);

  const handleRepairFolder = () => {
    let url = null;
    if (record.assayType) {
      url = "/api/internal/assay/" + record.id + "/storage/repair";
    } else if (record.program) {
      url = "/api/internal/study/" + record.id + "/storage/repair";
    }
    if (!url) {
      notyf.open({
        type: "error",
        message: "Unable to repair folder. Unknown record type."
      });
      return;
    }
    setIsSubmitting(true);
    axios.post(url)
    .then(response => {
      notyf.open({
        type: "success",
        message: "Folder repaired successfully."
      })
      setIsRepaird(true);
      setIsSubmitting(false);
      navigate("#files");
      navigate(0);
    })
    .catch(error => {
      setIsSubmitting(false);
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to repair folder. Please try again."
      })
    });
  }

  useEffect(() => {

    // Check to see if the record has a primary folder
    const primaryFolderRef = record.storageFolders.find(f => f.primary);
    if (primaryFolderRef) {
      axios.get("/api/internal/data-files", {
        params: {
          path: primaryFolderRef.storageDriveFolder.path,
          folderId: primaryFolderRef.storageDriveFolder.id
        }
      })
      .then(response => {
        if (response.status === 200) {
          setFolder(response.data);
        }
      })
      .catch(error => {
        console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
    }
  }, [record]);

  console.debug("Primary Storage Folder", folder);

  if (isLoading) {
    return (
        <LoadingMessageWidget/>
    );
  } else if (!folder) {
    return (
        <IllustrationWidget
            image={"/static/images/clip/information-flow-yellow.png"}
            header={"No primary storage folder found"}
            color={"warning"}
            body={(
                <Button
                    variant={"warning"}
                    onClick={handleRepairFolder}
                    disabled={isSubmitting || isRepaird}
                >
                  <FontAwesomeIcon icon={faRefresh} className={"me-2"} />
                  {isSubmitting ? "Repairing folder..." : "Repair folder"}
                </Button>
            )}
        />
    );
  } else {
    return (
        <IconWidget
            icon={faFileLines}
            header={"Primary file storage"}
            body={(
                <>

                  <Button
                      variant={"primary"}
                      onClick={() => {
                        navigate("#files");
                        navigate(0);
                      }}
                      className={"mb-2"}
                  >
                    Browse files
                  </Button>

                  {
                    folder && !!folder.url ? (
                        <>
                          <br />
                          <Button
                              variant={"info"}
                              href={folder.url}
                              target={"_blank"}
                          >
                            View on web
                          </Button>
                        </>
                    ) : ""
                  }

                </>
            )}
            color={"primary"}
        />
    );
  }

}

PrimaryStorageFolderWidget.propTypes = {
  record: PropTypes.object.isRequired
}

export default PrimaryStorageFolderWidget;
