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

import React, {useContext, useState} from "react";
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
import {useDispatch} from "react-redux";
import {setTab} from "../../redux/tabSlice";
import {useMutation, useQuery} from "react-query";

const PrimaryStorageFolderWidget = ({record}) => {

  const primaryFolderRef = record.storageFolders.find(f => f.primary);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isRepaird, setIsRepaird] = useState(false);
  const navigate = useNavigate();
  const notyf = useContext(NotyfContext);
  const dispatch = useDispatch();

  const {data: folder, isLoading, error} = useQuery({
    queryKey: ["recordStorageFolders", record.id],
    queryFn: () => {
      return axios.get("/api/internal/data-files", {
        params: {
          path: primaryFolderRef.storageDriveFolder.path,
          folderId: primaryFolderRef.storageDriveFolder.id
        }
      })
      .then(response => response.data);
    },
    enabled: !!primaryFolderRef
  });

  const repairMutation = useMutation(() => {
    let url = null;
    if (record.assayType) {
      url = `/api/internal/assay/${record.id}/storage/repair`;
    } else if (record.program) {
      url = `/api/internal/study/${record.id}/storage/repair`;
    } else {
      url = `/api/internal/program/${record.id}/storage/repair`;
    }
    return axios.post(url)
  }, {
    onMutate: () => {
      setIsSubmitting(true);
    },
    onSuccess: () => {
      notyf.success("Folder repaired successfully.");
      setIsRepaird(true);
      navigate("#files");
      navigate(0);
    },
    onError: (e) => {
      console.error(e);
      notyf.error("Failed to repair folder: " + e.message);
    },
    onSettled: () => {
      setIsSubmitting(false);
    }
  })

  console.debug("Primary Storage Folder", folder);

  if (isLoading) return <LoadingMessageWidget/>

  if (!folder) {
    return (
        <IllustrationWidget
            image={"/static/images/clip/information-flow-yellow.png"}
            header={"No primary storage folder found"}
            color={"warning"}
            body={(
                <Button
                    variant={"warning"}
                    onClick={() => repairMutation.mutate()}
                    disabled={isSubmitting || isRepaird}
                >
                  <FontAwesomeIcon icon={faRefresh} className={"me-2"} />
                  {isSubmitting ? "Creating folder..." : "Create folder"}
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
                        dispatch(setTab("files"));
                        navigate("#files")
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
