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
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import AWSIntegrationDetailsCard from "./AWSIntegrationDetailsCard";
import AWSIntegrationSetupCard from "./AWSIntegrationSetupCard";
import S3BucketCard from "./S3BucketCard";

const AWSIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [drives, setDrives] = useState([]);
  const [loadCount, setLoadCount] = useState(0);
  const notyf = useContext(NotyfContext);

  useEffect(() => {
    axios.get("/api/internal/integrations/aws")
    .then(response => {

      console.debug("AWS settings loaded", response.data);

      if (response.data.length === 0) {
        console.warn("No AWS settings found");
        setSettings(null);
      } else if (response.data.length === 1) {
        setSettings(response.data[0]);
      } else {
        console.warn("Multiple AWS settings found", response.data);
        setSettings(response.data[0]);
      }

      axios.get("/api/internal/drives/s3/")
      .then(response => {
        console.debug("S3 buckets loaded", response.data);
        setDrives(response.data);
      })

    })
    .catch(error => {
      console.error("Failed to load AWS settings", error);
      notyf.open({
        type: "error",
        message: "Failed to load AWS settings"
      });
    });
  }, [loadCount]);

  return (
      <>

        {
          settings
              ? <AWSIntegrationDetailsCard settings={settings} />
              : <AWSIntegrationSetupCard />
        }

        {
          drives.length > 0 && (
              drives.map(drive => <S3BucketCard bucket={drive} />)
          )
        }

      </>
  )
}

export default AWSIntegrationSettings;