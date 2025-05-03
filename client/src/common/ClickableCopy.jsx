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

import React, {useContext, useRef, useState} from 'react';
import PropTypes from 'prop-types';
import {Copy} from 'react-feather';
import {OverlayTrigger, Tooltip} from "react-bootstrap";
import NotyfContext from "../context/NotyfContext";

function ClickableCopy({ text }) {
  const [showIcon, setShowIcon] = useState(false);
  const spanRef = useRef(null);
  const notyf = useContext(NotyfContext);

  const handleMouseEnter = () => {
    setShowIcon(true);
  };

  const handleMouseLeave = () => {
    setShowIcon(false);
  };

  const handleCopyClick = async () => {
    try {
      await navigator.clipboard.writeText(text);
      notyf.success({message: "Copied to clipboard"});
    } catch (err) {
      console.error('Failed to copy text: ', err);
    }
  };

  return (
      <OverlayTrigger overlay={<Tooltip>Click to copy</Tooltip>} placement="right">
        <div
            className={showIcon ? "text-info" : ""}
            style={{ position: 'relative', display: 'inline-block', cursor: 'pointer' }}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
            onClick={handleCopyClick}
        >
          <span ref={spanRef}>{text}</span>
          <span className={(showIcon ? "text-info" : "text-muted") + " ms-2 me-2"}>
            <Copy size={16} />
          </span>
        </div>
      </OverlayTrigger>
  );
}

ClickableCopy.propTypes = {
  text: PropTypes.string.isRequired,
}

export default ClickableCopy;
