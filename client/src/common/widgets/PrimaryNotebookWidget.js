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
import {faBook, faRefresh} from "@fortawesome/free-solid-svg-icons";
import NotyfContext from "../../context/NotyfContext";
import IconWidget from "./IconWidget";
import {useDispatch} from "react-redux";
import {setTab} from "../../redux/tabSlice";

const PrimaryNotebookWidget = ({record}) => {

  const notebook = record && record.notebookFolder ? record.notebookFolder : null;
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isRepaird, setIsRepaird] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const notyf = useContext(NotyfContext);
  const dispatch = useDispatch();

  const handleRepairNotebook = () => {
    let url = null;
    if (record.assayType) {
      url = "/api/internal/assay/" + record.id + "/notebook/repair";
    } else if (record.program) {
      url = "/api/internal/study/" + record.id + "/notebook/repair";
    } else {
      url = "/api/internal/program/" + record.id + "/notebook/repair";
    }
    if (!url) {
      notyf.open({
        type: "error",
        message: "Unable to repair notebook. Unknown record type."
      });
      return;
    }
    setIsSubmitting(true);
    axios.post(url)
    .then(response => {
      notyf.open({
        type: "success",
        message: "Notebook repaired successfully."
      })
      setIsRepaird(true);
      setIsSubmitting(false);
      navigate(0);
    })
    .catch(error => {
      setIsSubmitting(false);
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to repair notebook. Please try again."
      })
    });
  }

  console.debug("Primary Notebook", notebook);

  if (isLoading) {
    return (
        <LoadingMessageWidget/>
    );
  } else if (!notebook) {
    return (
        <IllustrationWidget
            image={"/static/images/clip/information-flow-yellow.png"}
            header={"No ELN folder found"}
            color={"warning"}
            body={(
                <Button
                    variant={"warning"}
                    onClick={handleRepairNotebook}
                    disabled={isSubmitting || isRepaird}
                >
                  <FontAwesomeIcon icon={faRefresh} className={"me-2"} />
                  {isSubmitting ? "Creating notebook..." : "Create notebook"}
                </Button>
            )}
        />
    );
  } else {
    return (
        <IconWidget
            icon={faBook}
            header={"Electronic Laboratory Notebook"}
            body={(
                <>

                  <Button
                      variant={"primary"}
                      onClick={() => {
                        dispatch(setTab("notebook"));
                        navigate("#notebook")
                      }}
                      className={"mb-2"}
                  >
                    Browse contents
                  </Button>

                  {
                    notebook && !!notebook.url ? (
                        <>
                          <br />
                          <Button
                              variant={"info"}
                              href={notebook.url}
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

PrimaryNotebookWidget.propTypes = {
  record: PropTypes.object.isRequired
}

export default PrimaryNotebookWidget;
