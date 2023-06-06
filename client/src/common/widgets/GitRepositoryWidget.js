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
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";
import IconWidget from "./IconWidget";
import {faGitAlt, faGithubSquare, faGitlabSquare} from "@fortawesome/free-brands-svg-icons";

const getRepositoryIcon = (repository) => {
  let icon = faGitAlt;
  if (repository.gitGroup && repository.gitGroup.gitServiceType) {
    switch (repository.gitGroup.gitServiceType) {
      case "GITLAB":
        icon = faGitlabSquare;
        break;
      case "GITHUB":
        icon = faGithubSquare;
        break;
      default:
        icon = faGitAlt;
    }
  }
  return icon;
}

const GitRepositoryWidget = ({record}) => {

  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const notyf = useContext(NotyfContext);

  // const handleCreateRepository = () => {
  //   let url = null;
  //   if (record.assayType) {
  //     url = "/api/internal/assay/" + record.id + "/git";
  //   } else if (record.program) {
  //     url = "/api/internal/study/" + record.id + "/git";
  //   }
  //   if (!url) {
  //     notyf.open({
  //       type: "error",
  //       message: "Unable to create git repository. Unknown record type."
  //     });
  //     return;
  //   }
  //   setIsSubmitting(true);
  //   axios.post(url)
  //   .then(response => {
  //     notyf.open({
  //       type: "success",
  //       message: "Git repository created successfully."
  //     })
  //     setIsSubmitting(false);
  //     navigate("#overview");
  //     navigate(0);
  //   })
  //   .catch(error => {
  //     setIsSubmitting(false);
  //     console.error(error);
  //     notyf.open({
  //       type: "error",
  //       message: "Failed to create Git repository. Please try again."
  //     })
  //   });
  // }

  const repository = record.gitRepositories.length > 0 ? record.gitRepositories[0] : null;

  if (!repository) {
    // return (
    //     <IllustrationWidget
    //         image={"/static/images/clip/information-flow-yellow.png"}
    //         header={"No Git repositories found."}
    //         color={"warning"}
    //         body={(
    //             <Button
    //                 variant={"warning"}
    //                 onClick={handleCreateRepository}
    //                 disabled={isSubmitting}
    //             >
    //               <FontAwesomeIcon icon={faRefresh} className={"me-2"} />
    //               {isSubmitting ? "Creating repository..." : "Create repository"}
    //             </Button>
    //         )}
    //     />
    // );
    return ('')
  } else {
    return (
        <IconWidget
            icon={getRepositoryIcon(repository)}
            header={"Git repository"}
            body={(
                <>

                  <Button
                      variant={"info"}
                      href={repository.webUrl}
                      target={"_blank"}
                  >
                    View on web
                  </Button>

                </>
            )}
            color={"primary"}
        />
    );
  }

}

GitRepositoryWidget.propTypes = {
  record: PropTypes.object.isRequired
}

export default GitRepositoryWidget;
